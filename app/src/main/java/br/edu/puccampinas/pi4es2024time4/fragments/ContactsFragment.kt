package br.edu.puccampinas.pi4es2024time4.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.puccampinas.pi4es2024time4.activities.MessagesActivity
import br.edu.puccampinas.pi4es2024time4.adapters.ContactsAdapter
import br.edu.puccampinas.pi4es2024time4.databinding.FragmentContactsBinding
import br.edu.puccampinas.pi4es2024time4.model.User
import br.edu.puccampinas.pi4es2024time4.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    private lateinit var snapshotEvent: ListenerRegistration
    private lateinit var adapterContacts: ContactsAdapter
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentContactsBinding.inflate(
            inflater, container, false
        )

        adapterContacts = ContactsAdapter { user ->
            val intent = Intent(context, MessagesActivity::class.java)
            intent.putExtra("dadosDestinatario", user)
            //intent.putExtra("origem", Constants.ORIGEM_CONTATO)
            startActivity( intent )
        }
        binding.rvContacts.adapter = adapterContacts
        binding.rvContacts.layoutManager = LinearLayoutManager(context)
        binding.rvContacts.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root

    }

    override fun onStart() {
        super.onStart()
        addContactsListener()
    }

    private fun addContactsListener() {

        snapshotEvent = firestore
            .collection( Constants.USERS )
            .addSnapshotListener { querySnapshot, error ->

                val contactsList = mutableListOf<User>()
                val documents = querySnapshot?.documents

                documents?.forEach { documentSnapshot ->

                    val loggedInUserId = firebaseAuth.currentUser?.uid
                    val user = documentSnapshot.toObject( User::class.java )

                    if( user != null && loggedInUserId != null ){
                        if( loggedInUserId != user.id ){
                            contactsList.add( user )
                        }
                    }

                }

                //Lista de contatos (atualizar o RecyclerView)
                if ( contactsList.isNotEmpty() ){
                    adapterContacts.addToList( contactsList )
                }

            }

    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotEvent.remove()
    }

}