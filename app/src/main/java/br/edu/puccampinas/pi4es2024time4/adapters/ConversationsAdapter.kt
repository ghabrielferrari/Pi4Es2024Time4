package br.edu.puccampinas.pi4es2024time4.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.edu.puccampinas.pi4es2024time4.databinding.ItemConversationsBinding
import br.edu.puccampinas.pi4es2024time4.model.Conversation
import com.squareup.picasso.Picasso

class ConversationsAdapter(
    private val onClick: (Conversation) -> Unit
): Adapter<ConversationsAdapter.ConversationViewHolder>() {

    private var conversationList = emptyList<Conversation>()
    fun addToList(list: List<Conversation>) {
        conversationList = list
        notifyDataSetChanged()
    }

    inner class ConversationViewHolder(
        private val binding: ItemConversationsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation) {

            binding.textConversationName.text = conversation.name
            binding.textConversationMessage.text = conversation.lastMessage
            Picasso.get()
                .load(conversation.picture)
                .into(binding.imageConversationPicture)

            //Evento de clique
            binding.clConversationItem.setOnClickListener {
                onClick(conversation)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemConversationsBinding.inflate(
            inflater, parent, false
        )
        return ConversationViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversationList[position]
        holder.bind(conversation)
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

}