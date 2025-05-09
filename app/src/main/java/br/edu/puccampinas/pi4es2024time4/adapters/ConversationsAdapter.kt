package com.jamiltondamasceno.aulawhatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.edu.puccampinas.pi4es2024time4.R
import br.edu.puccampinas.pi4es2024time4.databinding.ItemConversationsBinding
import br.edu.puccampinas.pi4es2024time4.model.Conversation
import com.squareup.picasso.Picasso

class ConversationsAdapter(
    private val onClick: (Conversation) -> Unit
) : Adapter<ConversationsAdapter.ConversasViewHolder>() {

    private var listaConversas = emptyList<Conversation>()
    fun adicionarLista( lista: List<Conversation> ){
        listaConversas = lista
        notifyDataSetChanged()
    }

    inner class ConversasViewHolder(
        private val binding: ItemConversationsBinding
    ) : RecyclerView.ViewHolder( binding.root ){

        fun bind( conversa: Conversation ){

            binding.textConversationName.text = conversa.name
            binding.textConversationMessage.text = conversa.lastMessage
            if (!conversa.picture.isNullOrEmpty()) {
                Picasso.get()
                    .load(conversa.picture)
                    .into(binding.imageConversationPicture)
            } else {
                // Carrega uma imagem padrão se a URL for nula ou vazia
                binding.imageConversationPicture.setImageResource(R.drawable.profile)
            }

            //Evento de clique
            binding.clConversationItem.setOnClickListener {
                onClick( conversa )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversasViewHolder {

        val inflater = LayoutInflater.from( parent.context )
        val itemView = ItemConversationsBinding.inflate(
            inflater, parent, false
        )
        return ConversasViewHolder( itemView )

    }

    override fun onBindViewHolder(holder: ConversasViewHolder, position: Int) {
        val conversa = listaConversas[position]
        holder.bind( conversa )
    }

    override fun getItemCount(): Int {
        return listaConversas.size
    }

}