package br.edu.puccampinas.pi4es2024time4.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.edu.puccampinas.pi4es2024time4.databinding.ItemMessagesRecipientBinding
import br.edu.puccampinas.pi4es2024time4.databinding.ItemMessagesSenderBinding
import br.edu.puccampinas.pi4es2024time4.model.Message
import br.edu.puccampinas.pi4es2024time4.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter : Adapter<ViewHolder>() {

    private var listaMensagens = emptyList<Message>()
    fun adicionarLista( lista: List<Message> ){
        listaMensagens = lista
        notifyDataSetChanged()
    }

    class MensagensRemetenteViewHolder(//ViewHolder
        private val binding: ItemMessagesSenderBinding
    ) : ViewHolder( binding.root ){

        fun bind( mensagem: Message ){
            binding.textMessageSender.text = mensagem.message
        }

        companion object {
            fun inflarLayout( parent: ViewGroup ) : MensagensRemetenteViewHolder {

                val inflater = LayoutInflater.from( parent.context )
                val itemView = ItemMessagesSenderBinding.inflate(
                    inflater, parent, false
                )
                return MensagensRemetenteViewHolder( itemView )

            }
        }

    }

    class MensagensDestinatarioViewHolder(//ViewHolder
        private val binding: ItemMessagesRecipientBinding
    ) : ViewHolder( binding.root ){

        fun bind( mensagem: Message ){
            binding.textMessageRecipient.text = mensagem.message
        }

        companion object {
            fun inflarLayout( parent: ViewGroup ) : MensagensDestinatarioViewHolder {

                val inflater = LayoutInflater.from( parent.context )
                val itemView = ItemMessagesRecipientBinding.inflate(
                    inflater, parent, false
                )
                return MensagensDestinatarioViewHolder( itemView )

            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        val mensagem = listaMensagens[position]
        val idUsuarioLogado = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return if( idUsuarioLogado == mensagem.userId ){
            Constants.SENDER_TYPE
        }else{
            Constants.RECIPIENT_TYPE
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if( viewType == Constants.SENDER_TYPE )
            return MensagensRemetenteViewHolder.inflarLayout( parent )

        return MensagensDestinatarioViewHolder.inflarLayout( parent )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mensagem = listaMensagens[position]
        when( holder ){
            is MensagensRemetenteViewHolder -> holder.bind(mensagem)
            is MensagensDestinatarioViewHolder -> holder.bind(mensagem)
        }
        /*val mensagensRemetenteViewHolder = holder as MensagensRemetenteViewHolder
        mensagensRemetenteViewHolder.bind()*/

    }

    override fun getItemCount(): Int {
        return listaMensagens.size
    }

}