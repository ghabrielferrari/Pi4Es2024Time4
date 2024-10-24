package br.edu.puccampinas.pi4es2024time4.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.edu.puccampinas.pi4es2024time4.databinding.ItemMessagesRecipientBinding
import br.edu.puccampinas.pi4es2024time4.databinding.ItemMessagesSenderBinding
import br.edu.puccampinas.pi4es2024time4.model.Message
import br.edu.puccampinas.pi4es2024time4.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter : Adapter<ViewHolder>() {

    private var messagesList = emptyList<Message>()
    fun addToList(list: List<Message>) {
        messagesList = list
        notifyDataSetChanged()
    }

    class SenderMessagesViewHolder(
        private val binding: ItemMessagesSenderBinding
    ) : ViewHolder(binding.root) {

        fun bind(message: Message){
            binding.textMessageSender.text = message.message
        }

        companion object {
            fun inflateLayout(parent: ViewGroup): SenderMessagesViewHolder {

                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessagesSenderBinding.inflate(
                    inflater, parent, false
                )
                return SenderMessagesViewHolder(itemView)

            }
        }

    }

    class RecipientMessagesViewHolder(
        private val binding: ItemMessagesRecipientBinding
    ) : ViewHolder(binding.root) {

        fun bind(message: Message){
            binding.textMessageRecipient.text = message.message
        }

        companion object {
            fun inflateLayout(parent: ViewGroup): RecipientMessagesViewHolder {

                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessagesRecipientBinding.inflate(
                    inflater, parent, false
                )
                return RecipientMessagesViewHolder(itemView)

            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val message = messagesList[position]
        val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return if (loggedInUserId == message.userId) {
            Constants.SENDER_TYPE
        } else {
            Constants.RECIPIENT_TYPE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == Constants.SENDER_TYPE)
            return SenderMessagesViewHolder.inflateLayout(parent)

        return RecipientMessagesViewHolder.inflateLayout(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val message = messagesList[position]
        when(holder){
            is SenderMessagesViewHolder -> holder.bind(message)
            is RecipientMessagesViewHolder -> holder.bind(message)
        }
        /*val senderMessagesViewHolder = holder as SenderMessagesViewHolderSenderMessagesViewHolder
        senderMessagesViewHolder.bind()*/

    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

}