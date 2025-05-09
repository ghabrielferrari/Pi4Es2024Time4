package br.edu.puccampinas.pi4es2024time4.busca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.pi4es2024time4.R

// Modelo para representar as avaliações
data class Review(val username: String, val reviewText: String)

class CommentsAdapter(private val comments: MutableList<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.usernameTextView.text = comment.username
        holder.commentTextView.text = comment.reviewText
        holder.ratingBar.rating = comment.rating
    }

    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }
}






