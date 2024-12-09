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
            ForumItem(
                "- Como posso melhorar a velocidade da minha internet?",
                "Reinicie o modem e roteador. Tente reposicionar o roteador e minimize a quantidade de dispositivos conectados."),
            ForumItem(
                "- Meu computador está travando constantemente, o que fazer?",
                "Feche programas desnecessários e faça uma verificação de vírus."),
            ForumItem(
                "- Como configurar uma impressora no meu computador?",
                "Instale o driver da impressora e conecte via USB ou Wi-Fi."),
            ForumItem(
                "- Por que meu celular está esquentando muito?",
                "Feche apps em segundo plano e evite usar o celular em lugares muito quentes."),
            ForumItem(
                "- Como resolver problemas de áudio em meu computador?",
                "Verifique se o som está ativado e atualize os drivers de áudio."),
            ForumItem(
                "- Meu monitor está piscando, o que pode ser?",
                "Verifique os cabos de conexão e atualize os drivers gráficos."),
            ForumItem(
                "- Como corrigir erros de rede no meu roteador?",
                "Reinicie o roteador e verifique a conexão dos cabos."),
            ForumItem(
                "- O que fazer se minha máquina de lavar não está girando?",
                "Verifique se as roupas estão balanceadas e limpe o filtro."),

            )

        val recyclerView: RecyclerView = findViewById(R.id.rvForum)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ForumAdapter(forumList)
    }
}