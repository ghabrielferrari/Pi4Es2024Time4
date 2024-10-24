package br.edu.puccampinas.pi4es2024time4.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.puccampinas.pi4es2024time4.activities.MessagesActivity
import br.edu.puccampinas.pi4es2024time4.adapters.ConversationsAdapter
import br.edu.puccampinas.pi4es2024time4.databinding.FragmentConversationsBinding
import br.edu.puccampinas.pi4es2024time4.model.Conversation
import br.edu.puccampinas.pi4es2024time4.model.User
import br.edu.puccampinas.pi4es2024time4.utils.Constants
import br.edu.puccampinas.pi4es2024time4.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ConversationsFragment : Fragment() {

    private lateinit var binding: FragmentConversationsBinding
    private lateinit var snapshotEvent: ListenerRegistration
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var conversationsAdapter: ConversationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentConversationsBinding.inflate(
            inflater, container, false
        )

        conversationsAdapter = ConversationsAdapter { conversation ->
            val intent = Intent(context, MessagesActivity::class.java)

            val user = User(
                id = conversation.recipientUserId,
                name = conversation.name,
                picture = conversation.picture
            )
            intent.putExtra("dadosDestinatario", user)
            //intent.putExtra("origem", Constants.ORIGEM_CONVERSA)
            startActivity( intent )
        }
        binding.rvConversation.adapter = conversationsAdapter
        binding.rvConversation.layoutManager = LinearLayoutManager(context)
        binding.rvConversation.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addConversationsListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotEvent.remove()
    }

    private fun addConversationsListener() {

        val senderUserId= firebaseAuth.currentUser?.uid
        if( senderUserId != null ){
            snapshotEvent = firestore
                .collection(Constants.CONVERSATIONS)
                .document( senderUserId )
                .collection(Constants.LAST_MESSAGES)
                .orderBy("data", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->

                    if( error != null ){
                        activity?.showMessage("Erro ao recuperar conversas")
                    }

                    val conversationList = mutableListOf<Conversation>()
                    val documents = querySnapshot?.documents

                    documents?.forEach { documentSnapshot ->

                        val conversation = documentSnapshot.toObject(Conversation::class.java)
                        if( conversation != null ){
                            conversationList.add( conversation )
                            Log.i("exibicao_conversas", "${conversation.name} - ${conversation.lastMessage}")
                        }
                    }

                    if( conversationList.isNotEmpty() ){
                        conversationsAdapter.addToList( conversationList )
                    }

                }
        }

    }

}