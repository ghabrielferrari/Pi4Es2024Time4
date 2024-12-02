package br.edu.puccampinas.pi4es2024time4

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.pi4es2024time4.R
import br.edu.puccampinas.pi4es2024time4.ForumAdapter
import br.edu.puccampinas.pi4es2024time4.ForumItem

class ForunsDuvidaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foruns_duvida)

        // Exemplo de dados
        val forumList = listOf(
            ForumItem("Título 1", "Texto do Título 1"),
            ForumItem("Título 2", "Texto do Título 2"),
            ForumItem("Título 3", "Texto do Título 3")
        )

        val recyclerView: RecyclerView = findViewById(R.id.rvForum)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ForumAdapter(forumList)
    }
}