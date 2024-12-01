package br.edu.puccampinas.projeto

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.pi4es2024time4.R

class ServiceAdapter(
    private val context: Context,
    private val serviceList: List<Service>,
    private val onServiceClick: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceName: TextView = view.findViewById(R.id.serviceName)
        val providerName: TextView = view.findViewById(R.id.providerName)
        val serviceDescription: TextView = view.findViewById(R.id.serviceDescription)
        val openButton: Button = view.findViewById(R.id.openButton)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = serviceList[position]
        holder.serviceName.text = service.nome
        holder.providerName.text = service.provedor
        holder.serviceDescription.text = service.descricao
        holder.openButton.setOnClickListener {
            val intent = Intent(context, ActivityServico::class.java).apply {
                putExtra("service_name", service.nome)
                putExtra("provider_name", service.provedor)
                putExtra("service_description", service.descricao)
            }
            // Iniciar a ActivityServiço
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = serviceList.size
}
