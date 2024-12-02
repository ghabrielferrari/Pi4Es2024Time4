package br.edu.puccampinas.pi4es2024time4.busca

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.pi4es2024time4.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ServiceDetailActivity : AppCompatActivity() {

    private val commentsList = mutableListOf<Comment>()  // Lista local para armazenar os comentários
    private lateinit var commentsAdapter: CommentsAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_detail)

        // Recuperando os dados passados pela Intent
        val serviceTitle: String? = intent.getStringExtra("SERVICE_TITLE")
        val serviceDescription: String? = intent.getStringExtra("SERVICE_DESCRIPTION")

        // Referências aos TextViews
        val titleTextView: TextView = findViewById(R.id.serviceTitleTextView)
        val descriptionTextView: TextView = findViewById(R.id.serviceDescriptionTextView)
        val ratingBar: RatingBar = findViewById(R.id.ratingBar)
        val hireServiceButton: Button = findViewById(R.id.hireServiceButton)

        // Atribuindo valores aos TextViews
        titleTextView.text = serviceTitle ?: "Título não disponível"
        descriptionTextView.text = serviceDescription ?: "Descrição não disponível"

        val commentsRecyclerView: RecyclerView = findViewById(R.id.commentsRecyclerView)
        commentsAdapter = CommentsAdapter(commentsList) // Use mutable list
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentsRecyclerView.adapter = commentsAdapter


        // Carregar comentários específicos deste serviço, caso existam
        if (serviceTitle != null) {
            loadComments(serviceTitle)
        }

        // Ação ao clicar no botão "Contratar Serviço"
        hireServiceButton.setOnClickListener {
            val paymentIntent = Intent(this, PaymentMethodActivity::class.java)
            startActivity(paymentIntent)
        }

        // Definir a ação para o RatingBar do serviço
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            Toast.makeText(this, "Você avaliou com: $rating estrelas", Toast.LENGTH_SHORT).show()
        }

        // Referência para o campo de comentário
        val commentEditText: EditText = findViewById(R.id.editTextComment)
        val submitCommentButton: Button = findViewById(R.id.buttonSubmitComment)

        // Ação ao clicar no botão de enviar comentário
        submitCommentButton.setOnClickListener {
            val commentText = commentEditText.text.toString()
            val rating = ratingBar.rating // Captura a avaliação do RatingBar

            if (commentText.isNotEmpty()) {
                // Nome fixo "pi4" ao invés de nome do usuário
                val newComment = Comment("pi4", commentText, rating)

                // Adicionando o comentário à lista local
                commentsList.add(newComment)

                // Atualizando a lista do RecyclerView
                commentsAdapter.notifyItemInserted(commentsList.size - 1)

                // Salvando os comentários atualizados para este serviço
                if (serviceTitle != null) {
                    saveComments(serviceTitle)
                }

                // Limpar o campo após o envio
                commentEditText.text.clear()

                // Exibir uma mensagem de sucesso
                Toast.makeText(this, "Comentário enviado com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                // Exibir uma mensagem de erro caso o campo de comentário esteja vazio
                Toast.makeText(this, "Por favor, insira um comentário.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função para salvar os comentários no SharedPreferences, usando o título do serviço como chave
    private fun saveComments(serviceTitle: String) {
        val sharedPreferences = getSharedPreferences("comments_pref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convertendo a lista de comentários para JSON com Gson
        val gson = Gson()
        val json = gson.toJson(commentsList)

        // Salvando os comentários usando o título do serviço como chave
        editor.putString(serviceTitle, json)
        editor.apply()
    }

    // Função para carregar os comentários do SharedPreferences com base no título do serviço
    private fun loadComments(serviceTitle: String) {
        val sharedPreferences = getSharedPreferences("comments_pref", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(serviceTitle, null)

        if (json != null) {
            val type = object : TypeToken<List<Comment>>() {}.type
            val loadedComments: List<Comment> = gson.fromJson(json, type)

            // Atualizando a lista de comentários
            commentsList.clear()
            commentsList.addAll(loadedComments)
            commentsAdapter.notifyDataSetChanged()
        }
    }
}
