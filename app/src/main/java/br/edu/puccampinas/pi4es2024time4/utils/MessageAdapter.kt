package br.edu.puccampinas.pi4es2024time4.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.pi4es2024time4.R
import br.edu.puccampinas.pi4es2024time4.utils.Message

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.tvMessage.text = if (message.isBot) {message.text} else {message.text}

        if (message.isBot) {
            holder.tvMessage.setBackgroundColor(Color.parseColor("#90EE90")) // Cor de fundo para mensagens do bot
            holder.tvMessage.setBackgroundResource(R.drawable.message_background_bot)
            holder.tvMessage.setTextColor(Color.parseColor("#000000")) // Cor do texto do bot
        } else {
            holder.tvMessage.setBackgroundColor(Color.parseColor("#FFA500")) // Cor de fundo para mensagens do usuário
            holder.tvMessage.setBackgroundResource(R.drawable.message_background_user)
            holder.tvMessage.setTextColor(Color.parseColor("#000000")) // Cor do texto do usuário
        }
    }

    override fun getItemCount(): Int = messages.size
}
