package br.edu.puccampinas.pi4es2024time4.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.puccampinas.pi4es2024time4.R
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
import java.io.File
import java.io.IOException

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
    private var dadosDestinatario: User? = null
    private var dadosUsuarioRementente: User? = null
    private lateinit var conversasAdapter: MessagesAdapter

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var audioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        recuperarDadosUsuarios()
        inicializarToolbar()
        inicializarEventoClique()
        inicializarRecyclerview()
        inicializarListeners()
    }

    private fun inicializarRecyclerview() {

        with(binding){
            conversasAdapter = MessagesAdapter()
            rvMessages.adapter = conversasAdapter
            rvMessages.layoutManager = LinearLayoutManager(applicationContext)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
        stopRecording()
    }

    private fun inicializarListeners() {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id
        if( idUsuarioRemetente != null && idUsuarioDestinatario != null ){

            listenerRegistration = firestore
                .collection(Constants.MESSAGES)
                .document( idUsuarioRemetente )
                .collection( idUsuarioDestinatario )
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, erro ->

                    if( erro != null ){
                        showMessage("Erro ao recuperar mensagens")
                    }

                    val listaMensagens = mutableListOf<Message>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach { documentSnapshot ->
                        val mensagem = documentSnapshot.toObject( Message::class.java )
                        if( mensagem != null ){
                            listaMensagens.add( mensagem )
                            Log.i("exibicao_mensagens", mensagem.message)
                        }
                    }

                    //Lista
                    if( listaMensagens.isNotEmpty() ){
                        //Carregar os dados Adapter
                        conversasAdapter.adicionarLista( listaMensagens )
                    }

                }

        }

    }

    private fun inicializarEventoClique() {

        binding.fabSend.setOnClickListener {
            val mensagem = binding.editMessage.text.toString()
            salvarMensagem( mensagem )
        }

        binding.ibMic.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        binding.ibCamera.setOnClickListener {
            openCamera()
        }

    }

    private fun startRecording() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_AUDIO_PERMISSION)
            return
        }
        audioFile = File.createTempFile("audio_", ".3gp", cacheDir)  // Arquivo temporário para gravação
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile!!.absolutePath)

            try {
                prepare()
                start()
                isRecording = true
                binding.ibMic.setImageResource(R.drawable.ic_mic_24) // Altere para o ícone de parar
            } catch (e: IOException) {
                showMessage("Erro ao iniciar gravação")
                Log.e("AudioRecord", "prepare() failed")
            }
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            mediaRecorder?.apply {
                stop()
                release()
                isRecording = false
                binding.ibMic.setImageResource(R.drawable.ic_mic_24) // Altere para o ícone de microfone
                enviarMensagemAudio()
            }
        }
    }

    private fun enviarMensagemAudio() {
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id
        if (idUsuarioRemetente != null && idUsuarioDestinatario != null && audioFile != null) {
            val mensagemAudio = Message(
                userId = idUsuarioRemetente,
                message = audioFile!!.absolutePath // ou o URI do arquivo
            )
            salvarMensagemFirestore(idUsuarioRemetente, idUsuarioDestinatario, mensagemAudio)
            salvarMensagemFirestore(idUsuarioDestinatario, idUsuarioRemetente, mensagemAudio)
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            enviarMensagemFoto(imageUri)
        }
    }

    private fun enviarMensagemFoto(imageUri: Uri?) {
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id
        if (idUsuarioRemetente != null && idUsuarioDestinatario != null && imageUri != null) {
            val mensagemFoto = Message(
                userId = idUsuarioRemetente,
                message = imageUri.toString() // ou outra representação da imagem
            )
            salvarMensagemFirestore(idUsuarioRemetente, idUsuarioDestinatario, mensagemFoto)
            salvarMensagemFirestore(idUsuarioDestinatario, idUsuarioRemetente, mensagemFoto)
        }
    }

    private fun salvarMensagem(textoMensagem: String) {
        if (textoMensagem.isNotEmpty()) {
            val idUsuarioRemetente = firebaseAuth.currentUser?.uid
            val idUsuarioDestinatario = dadosDestinatario?.id
            if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {
                val mensagem = Message(
                    idUsuarioRemetente, textoMensagem
                )

                // Salvar para o Remetente
                salvarMensagemFirestore(
                    idUsuarioRemetente, idUsuarioDestinatario, mensagem
                )
                // Jamilton -> Foto e nome Destinatario (ana)
                val conversaRemetente = Conversation(
                    idUsuarioRemetente, idUsuarioDestinatario,
                    dadosDestinatario!!.picture, dadosDestinatario!!.name,
                    textoMensagem
                )
                salvarConversaFirestore(conversaRemetente)

                // Salvar mesma mensagem para o destinatario
                salvarMensagemFirestore(
                    idUsuarioDestinatario, idUsuarioRemetente, mensagem
                )
                // Ana -> Foto e nome Remente (jamilton)
                val conversaDestinatario = Conversation(
                    idUsuarioDestinatario, idUsuarioRemetente,
                    dadosUsuarioRementente!!.picture, dadosUsuarioRementente!!.name,
                    textoMensagem
                )
                salvarConversaFirestore(conversaDestinatario)

                binding.editMessage.setText("")
            }
        }
    }

    private fun salvarConversaFirestore(conversa: Conversation) {
        firestore
            .collection(Constants.CONVERSATIONS)
            .document(conversa.senderUserId)
            .collection(Constants.LAST_MESSAGES)
            .document(conversa.recipientUserId)
            .set(conversa)
            .addOnFailureListener {
                showMessage("Erro ao salvar conversa")
            }
    }

    private fun salvarMensagemFirestore(
        idUsuarioRemetente: String,
        idUsuarioDestinatario: String,
        mensagem: Message
    ) {
        firestore
            .collection(Constants.MESSAGES)
            .document(idUsuarioRemetente)
            .collection(idUsuarioDestinatario)
            .add(mensagem)
            .addOnFailureListener {
                showMessage("Erro ao enviar mensagem")
            }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.tbMessages
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if (dadosDestinatario != null) {
                binding.textName.text = dadosDestinatario!!.name

                // Verificar se a URL da imagem não é nula ou vazia antes de carregar
                val pictureUrl = dadosDestinatario!!.picture
                if (!pictureUrl.isNullOrEmpty()) {
                    Picasso.get()
                        .load(pictureUrl)
                        .into(binding.imageProfilePicture)
                } else {
                    // Carregar uma imagem padrão caso a URL esteja vazia
                    Picasso.get()
                        .load(R.drawable.profile)
                        .into(binding.imageProfilePicture)
                }
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }


    private fun recuperarDadosUsuarios() {

        //Dados do usuário logado
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        if( idUsuarioRemetente != null ){
            firestore
                .collection(Constants.USERS)
                .document( idUsuarioRemetente )
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val usuario = documentSnapshot.toObject(User::class.java)
                    if( usuario != null ){
                        dadosUsuarioRementente = usuario
                    }

                }
        }

        //Recuperando dados destinatário
        val extras = intent.extras
        if( extras != null ){
            dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

    companion object {
        private const val REQUEST_AUDIO_PERMISSION = 200
    }

}