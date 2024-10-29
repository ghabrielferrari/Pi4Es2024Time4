package br.edu.puccampinas.pi4es2024time4.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.edu.puccampinas.pi4es2024time4.R
import br.edu.puccampinas.pi4es2024time4.databinding.ItemContactsBinding
import br.edu.puccampinas.pi4es2024time4.model.User
import com.squareup.picasso.Picasso

class ContactsAdapter(
    private val onClick: (User) -> Unit
) : Adapter<ContactsAdapter.ContactsViewHolder>() {

    private var contactsList = emptyList<User>()

    fun addToList(list: List<User>) {
        contactsList = list
        notifyDataSetChanged()
    }

    inner class ContactsViewHolder(
        private val binding: ItemContactsBinding
    ) : ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.textContactName.text = user.name

            // Verifique se a URL da imagem não está vazia
            if (!user.picture.isNullOrEmpty()) {
                Picasso.get()
                    .load(user.picture)
                    .into(binding.imageContactPicture)
            } else {
                // Defina uma imagem padrão caso a URL esteja vazia
                binding.imageContactPicture.setImageResource(R.drawable.profile)
            }

            // Evento de clique
            binding.clItemContato.setOnClickListener {
                onClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContactsBinding.inflate(inflater, parent, false)
        return ContactsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val user = contactsList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }
}