package br.edu.puccampinas.pi4es2024time4.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.puccampinas.pi4es2024time4.adapters.MessagesAdapter
import br.edu.puccampinas.pi4es2024time4.databinding.ActivityMessagesBinding
import br.edu.puccampinas.pi4es2024time4.model.Conversation
import br.edu.puccampinas.pi4es2024time4.model.Message
import br.edu.puccampinas.pi4es2024time4.model.User
import br.edu.puccampinas.pi4es2024time4.utils.Constants
import br.edu.puccampinas.pi4es2024time4.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class MessagesActivity : AppCompatActivity() {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val binding by lazy {
        ActivityMessagesBinding.inflate( layoutInflater )
    }
    private lateinit var listenerRegistration: ListenerRegistration
    private var dataRecipient: User? = null
    private var dataUserSender: User? = null
    private lateinit var adapterConversation: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        retrieveUserData()
        initializeToolbar()
        initializeClickEvent()
        initializeRecyclerView()
        initializeListeners()
    }

    private fun initializeRecyclerView() {

        with(binding){
            adapterConversation = MessagesAdapter()
            rvMessages.adapter = adapterConversation
            rvMessages.layoutManager = LinearLayoutManager(applicationContext)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun initializeListeners() {

        val senderUserId = firebaseAuth.currentUser?.uid
        val recipientUserId = dataRecipient?.id
        if( senderUserId != null && recipientUserId != null ){

            listenerRegistration = firestore
                .collection(Constants.MESSAGES)
                .document( senderUserId )
                .collection( recipientUserId )
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, error ->

                    if( error != null ){
                        showMessage("Erro ao recuperar mensagens")
                    }

                    val messageList = mutableListOf<Message>()
                    val documents = querySnapshot?.documents

                    documents?.forEach { documentSnapshot ->
                        val message = documentSnapshot.toObject( Message::class.java )
                        if( message != null ){
                            messageList.add( message )
                            Log.i("exibicao_mensagens", message.message)
                        }
                    }

                    //Lista
                    if( messageList.isNotEmpty() ){
                        //Carregar os dados Adapter
                        adapterConversation.addToList( messageList )
                    }

                }

        }

    }

    private fun initializeClickEvent() {

        binding.fabSend.setOnClickListener {
            val message = binding.editMessage.text.toString()
            saveMessage( message )
        }

    }

    private fun saveMessage(textMessage: String ) {
        if( textMessage.isNotEmpty() ){

            val senderUserId = firebaseAuth.currentUser?.uid
            val recipientUserId = dataRecipient?.id
            if( senderUserId != null && recipientUserId != null ){
                val message = Message(
                    senderUserId, textMessage
                )

                //Salvar para o Remetente
                saveMessageFirestore(
                    senderUserId, recipientUserId, message
                )
                //Jamilton -> Foto e nome Destinatario (ana)
                val senderConversation = Conversation(
                    senderUserId, recipientUserId,
                    dataRecipient!!.picture, dataRecipient!!.name,
                    textMessage
                )
                saveConversationFirestore( senderConversation )

                //Salvar mesma mensagem para o destinatario
                saveMessageFirestore(
                    recipientUserId, senderUserId, message
                )
                //Ana -> Foto e nome Remente (jamilton)
                val recipientConversation = Conversation(
                    recipientUserId, senderUserId,
                    dataUserSender!!.picture, dataUserSender!!.name,
                    textMessage
                )
                saveConversationFirestore( recipientConversation )

                binding.editMessage.setText("")

            }

        }
    }

    private fun saveConversationFirestore(conversa: Conversation) {
        firestore
            .collection(Constants.CONVERSATIONS)
            .document( conversa.senderUserId )
            .collection(Constants.LAST_MESSAGES)
            .document( conversa.recipientUserId )
            .set( conversa )
            .addOnFailureListener {
                showMessage("Erro ao salvar conversa")
            }

    }

    private fun saveMessageFirestore(
        idUsuarioRemetente: String,
        idUsuarioDestinatario: String,
        message: Message
    ) {

        firestore
            .collection(Constants.MESSAGES)
            .document( idUsuarioRemetente )
            .collection( idUsuarioDestinatario )
            .add( message )
            .addOnFailureListener {
                showMessage("Erro ao enviar message")
            }

    }

    private fun initializeToolbar() {
        val toolbar = binding.tbMessages
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = ""
            if( dataRecipient != null ){
                binding.textName.text = dataRecipient!!.name
                Picasso.get()
                    .load(dataRecipient!!.picture)
                    .into( binding.imageProfilePicture )
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun retrieveUserData() {

        //Dados do usuário logado
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        if( idUsuarioRemetente != null ){
            firestore
                .collection(Constants.USERS)
                .document( idUsuarioRemetente )
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val user = documentSnapshot.toObject(User::class.java)
                    if( user != null ){
                        dataUserSender = user
                    }

                }
        }

        //Recuperando dados destinatário
        val extras = intent.extras
        if( extras != null ){
            dataRecipient = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable(
                    "dadosDestinatario",
                    User::class.java
                )
            }else{
                extras.getParcelable(
                    "dadosDestinatario"
                )
            }
        }

    }
}