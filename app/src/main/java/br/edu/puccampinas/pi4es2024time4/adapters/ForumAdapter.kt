package br.edu.puccampinas.pi4es2024time4.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.pi4es2024time4.R

data class ForumItem(val titulo: String, val texto: String)

class ForumAdapter(private val forumList: List<ForumItem>) :
    RecyclerView.Adapter<ForumAdapter.ForumViewHolder>() {

    inner class ForumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTituloForum: TextView = itemView.findViewById(R.id.textTituloForum)
        val textForum: TextView = itemView.findViewById(R.id.textForum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum, parent, false)
        return ForumViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForumViewHolder, position: Int) {
        val item = forumList[position]
        holder.textTituloForum.text = item.titulo
        holder.textForum.text = item.texto
    }

    override fun getItemCount(): Int = forumList.size
}
