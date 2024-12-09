package br.edu.puccampinas.pi4es2024time4.busca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.pi4es2024time4.R

// Adapter para exibir os serviços
class ServicoAdapter(private var serviceList: List<ServiceItem>, private val onServiceClick: (ServiceItem) -> Unit) :
    RecyclerView.Adapter<ServicoAdapter.ServiceViewHolder>() {

    // ViewHolder que acessa as views do layout do item e lida com cliques
    class ServiceViewHolder(itemView: View, private val onServiceClick: (ServiceItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(serviceItem: ServiceItem) {
            nameTextView.text = serviceItem.title
            descriptionTextView.text = serviceItem.description
            itemView.setOnClickListener {
                onServiceClick(serviceItem)  // Chama o callback passando o serviço selecionado
            }
        }
    }

    // Infla o layout do item e cria o ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_servico, parent, false)
        return ServiceViewHolder(itemView, onServiceClick)
    }

    // Atualiza os dados da lista no RecyclerView
    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val serviceItem = serviceList[position]
        holder.bind(serviceItem)  // Chama a função bind para configurar a view
    }

    override fun getItemCount(): Int = serviceList.size
}
