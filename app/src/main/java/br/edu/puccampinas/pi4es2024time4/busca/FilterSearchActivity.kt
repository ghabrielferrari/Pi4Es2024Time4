package br.edu.puccampinas.pi4es2024time4.busca

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.text.TextWatcher
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.pi4es2024time4.R

class FilterSearchActivity : AppCompatActivity() {

    private lateinit var serviceList: List<ServiceItem>  // Lista de serviços original
    private lateinit var filteredList: MutableList<ServiceItem> // Lista filtrada (Mutable para adicionar/remover)
    private lateinit var serviceRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var adapter: ServicoAdapter
    private lateinit var filterButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_search)

        // Inicializando os controles
        serviceList = listOf(
            ServiceItem("Instalação de Chuveiro", "Serviço de instalação de chuveiros"),
            ServiceItem("Reparo de Torneira", "Serviço de reparo de vazamentos e manutenção"),
            ServiceItem("Manutenção de Ar Condicionado", "Serviço de manutenção em sistemas de ar condicionado"),
            ServiceItem("Instalação de Cozinha", "Instalação de sistemas elétricos para cozinha"),
            ServiceItem("Instalação de Fiação", "Serviço de instalação de fiação elétrica"),
            ServiceItem("Concerto de Chuveiro", "Serviço de reparo de chuveiro")
        )

        // Inicializa a lista filtrada
        filteredList = serviceList.toMutableList()

        // Inicializa as views
        serviceRecyclerView = findViewById(R.id.serviceRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)

        // Configuração do RecyclerView
        adapter = ServicoAdapter(filteredList) { serviceItem ->
            // Ao clicar no item, abre a ServiceDetailActivity
            val intent = Intent(this, ServiceDetailActivity::class.java)
            Toast.makeText(this, "Item clicado: ${serviceItem.title}", Toast.LENGTH_SHORT).show()
            intent.putExtra("SERVICE_TITLE", serviceItem.title)
            intent.putExtra("SERVICE_DESCRIPTION", serviceItem.description)
            startActivity(intent)
        }
        serviceRecyclerView.layoutManager = LinearLayoutManager(this)
        serviceRecyclerView.adapter = adapter

        // Filtrar serviços conforme o texto digitado no EditText
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                try {
                    filterServices(query)
                } catch (e: Exception) {
                    e.printStackTrace()  // Exibe o erro no Log para debug
                }
            }
        })

        // Configurar botões de filtro
        filterButtons = listOf(
            findViewById(R.id.filterButton1),
            findViewById(R.id.filterButton2),
            findViewById(R.id.filterButton3),
            findViewById(R.id.filterButton4),
            findViewById(R.id.filterButton5),
            findViewById(R.id.filterButton6)
        )

        filterButtons.forEach { button ->
            button.setOnClickListener {
                val filterText = button.text.toString().lowercase()
                try {
                    filterServicesByTag(filterText)
                } catch (e: Exception) {
                    e.printStackTrace()  // Exibe o erro no Log para debug
                }
                Toast.makeText(this, "Filtro: ${button.text}", Toast.LENGTH_SHORT).show()
            }
        }

        // Adicionar um botão de limpar filtro
        val clearButton: Button = findViewById(R.id.clearButton)
        clearButton.setOnClickListener {
            searchEditText.text.clear()  // Limpa o campo de pesquisa
            filteredList.clear()
            filteredList.addAll(serviceList)  // Restaura a lista original
            adapter.notifyDataSetChanged()  // Notifica o adapter sobre a atualização
            Toast.makeText(this, "Filtros removidos", Toast.LENGTH_SHORT).show()
        }
    }

    // Filtra os serviços conforme a pesquisa no EditText
    private fun filterServices(query: String) {
        try {
            filteredList.clear() // Limpa a lista filtrada
            filteredList.addAll(serviceList.filter {
                it.title.lowercase().contains(query) || it.description.lowercase().contains(query)
            })

            // Exibe uma mensagem caso não haja resultados
            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Nenhum serviço encontrado.", Toast.LENGTH_SHORT).show()
            }

            adapter.notifyDataSetChanged()  // Notifica o adapter sobre as mudanças na lista
        } catch (e: Exception) {
            e.printStackTrace()  // Exibe o erro no Log para debug
        }
    }

    // Filtra os serviços baseados no texto do botão de filtro
    private fun filterServicesByTag(tag: String) {
        try {
            filteredList.clear() // Limpa a lista filtrada
            filteredList.addAll(serviceList.filter {
                it.title.lowercase().contains(tag) || it.description.lowercase().contains(tag)
            })

            // Exibe uma mensagem caso não haja resultados
            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Nenhum serviço encontrado.", Toast.LENGTH_SHORT).show()
            }

            adapter.notifyDataSetChanged()  // Notifica o adapter sobre as mudanças na lista
        } catch (e: Exception) {
            e.printStackTrace()  // Exibe o erro no Log para debug
        }
    }
}
