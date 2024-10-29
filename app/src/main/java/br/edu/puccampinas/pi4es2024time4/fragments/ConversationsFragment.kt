package com.jamiltondamasceno.aulawhatsapp.fragments

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
import br.edu.puccampinas.pi4es2024time4.databinding.FragmentConversationsBinding
import br.edu.puccampinas.pi4es2024time4.model.Conversation
import br.edu.puccampinas.pi4es2024time4.model.User
import br.edu.puccampinas.pi4es2024time4.utils.Constants
import br.edu.puccampinas.pi4es2024time4.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.jamiltondamasceno.aulawhatsapp.adapters.ConversationsAdapter

class ConversationsFragment : Fragment() {

    private lateinit var binding: FragmentConversationsBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var conversasAdapter: ConversationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentConversationsBinding.inflate(
            inflater, container, false
        )

        conversasAdapter = ConversationsAdapter { conversa ->
            val intent = Intent(context, MessagesActivity::class.java)

            val usuario = User(
                id = conversa.recipientUserId,
                name = conversa.name,
                picture = conversa.picture
            )
            intent.putExtra("dadosDestinatario", usuario)
            //intent.putExtra("origem", Constantes.ORIGEM_CONVERSA)
            startActivity( intent )
        }
        binding.rvConversation.adapter = conversasAdapter
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
        adicionarListenerConversas()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }

    private fun adicionarListenerConversas() {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        if( idUsuarioRemetente != null ){
            eventoSnapshot = firestore
                .collection(Constants.CONVERSATIONS)
                .document( idUsuarioRemetente )
                .collection(Constants.LAST_MESSAGES)
                .orderBy("data", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, erro ->

                    if( erro != null ){
                        activity?.showMessage("Erro ao recuperar conversas")
                    }

                    val listaConversas = mutableListOf<Conversation>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach { documentSnapshot ->

                        val conversa = documentSnapshot.toObject(Conversation::class.java)
                        if( conversa != null ){
                            listaConversas.add( conversa )
                            Log.i("exibicao_conversas", "${conversa.name} - ${conversa.lastMessage}")
                        }
                    }

                    if( listaConversas.isNotEmpty() ){
                        conversasAdapter.adicionarLista( listaConversas )
                    }

                }
        }

    }

}