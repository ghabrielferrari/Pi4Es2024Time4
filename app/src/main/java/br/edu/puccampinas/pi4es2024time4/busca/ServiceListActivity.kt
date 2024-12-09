package br.edu.puccampinas.pi4es2024time4.busca

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.pi4es2024time4.R

class ServiceListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_list)

        val searchIcon: ImageView = findViewById(R.id.searchIcon)
        val serviceRecyclerView: RecyclerView = findViewById(R.id.serviceRecyclerView)

        // Lista de serviços
        val serviceList = listOf(
            ServiceItem("Instalação de Chuveiro", "Serviço de instalação de chuveiros"),
            ServiceItem("Reparo de Torneira", "Serviço de reparo de vazamentos e manutenção"),
            ServiceItem("Manutenção de Ar Condicionado", "Serviço de manutenção em sistemas de ar condicionado"),
            ServiceItem("Instalação de Cozinha", "Instalação de sistemas elétricos para cozinha"),
            ServiceItem("Instalação de Fiação", "Serviço de instalação de fiação elétrica"),
            ServiceItem("Concerto de Chuveiro", "Serviço de reparo de chuveiro")
        )

        // Configuração do RecyclerView
        serviceRecyclerView.layoutManager = LinearLayoutManager(this)
        serviceRecyclerView.adapter = ServicoAdapter(serviceList) { serviceItem ->
            val intent = Intent(this, ServiceDetailActivity::class.java)
            intent.putExtra("SERVICE_TITLE", serviceItem.title)
            intent.putExtra("SERVICE_DESCRIPTION", serviceItem.description)
            startActivity(intent)
        }

        searchIcon.setOnClickListener {
            val intent = Intent(this, FilterSearchActivity::class.java)
            startActivity(intent)
        }
    }
}


