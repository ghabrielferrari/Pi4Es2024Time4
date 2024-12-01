package br.edu.puccampinas.pi4es2024time4.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.puccampinas.pi4es2024time4.databinding.ActivityChatBotBinding
import br.edu.puccampinas.pi4es2024time4.MessageChatBot
import br.edu.puccampinas.pi4es2024time4.MessageAdapter
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.runBlocking

class ChatBotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBotBinding
    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<MessageChatBot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eTPrompt = binding.eTPrompt
        val btnSubmit = binding.btnSubmit

        // Configurar RecyclerView
        adapter = MessageAdapter(messages)
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.adapter = adapter

        btnSubmit.setOnClickListener {
            val prompt = eTPrompt.text.toString()
            if (prompt.isNotEmpty()) {
                // Adiciona mensagem do usuário
                messages.add(MessageChatBot(prompt, isBot = false))
                adapter.notifyItemInserted(messages.size - 1)
                binding.recyclerViewMessages.scrollToPosition(messages.size - 1)

                // Limpar campo de entrada
                eTPrompt.text.clear()

                // Cria e usa o modelo gerativo
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = "AIzaSyCl2jXgTqiESuU2BGtrrIcySt22R1q2JJA"
                )

                runBlocking {
                    val response = generativeModel.generateContent(prompt)
                    // Adiciona resposta do bot
                    messages.add(MessageChatBot(response.text ?: "", isBot = true))
                    adapter.notifyItemInserted(messages.size - 1)
                    binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
                }
            }
        }
    }
}