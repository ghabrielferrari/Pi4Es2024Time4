package br.edu.puccampinas.pi4es2024time4.busca

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.pi4es2024time4.R
import br.edu.puccampinas.pi4es2024time4.activities.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DebitCardActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debit_card)

        // Inicializa a referência ao Firestore
        firestore = FirebaseFirestore.getInstance()

        // Referências para os EditText e Button
        val editTextCardNumber: EditText = findViewById(R.id.editTextCardNumber)
        val editTextExpiryDate: EditText = findViewById(R.id.editTextExpiryDate)
        val editTextCVV: EditText = findViewById(R.id.editTextCVV)
        val buttonSubmitCard: Button = findViewById(R.id.buttonSubmitCard)

        // Definindo o comportamento do botão
        buttonSubmitCard.setOnClickListener {
            val cardNumber = editTextCardNumber.text.toString()
            val expiryDate = editTextExpiryDate.text.toString()
            val cvv = editTextCVV.text.toString()

            // Validação dos campos
            if (cardNumber.length == 16 && expiryDate.matches(Regex("\\d{2}/\\d{2}")) && cvv.length == 3) {
                if (isValidExpiryDate(expiryDate)) {
                    // Criação do objeto Serviço
                    val serviceId = UUID.randomUUID().toString()
                    val serviceData = hashMapOf(
                        "idServico" to serviceId,
                        "servico" to "Pagamento com cartão de débito",
                        "dataRealizacao" to getCurrentDateTime(),
                        "status" to "Concluído"
                    )

                    // Salvar no Firestore
                    firestore.collection("Servicos")
                        .document(serviceId)
                        .set(serviceData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Dados do serviço salvos com sucesso!", Toast.LENGTH_SHORT).show()

                            // Redireciona para a MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao salvar os dados: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "A data de validade do cartão é inválida. Verifique a data.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Dados do cartão inválidos. Verifique e tente novamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função para validar a data de validade
    private fun isValidExpiryDate(expiryDate: String): Boolean {
        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR) % 100 // Obtém os dois últimos dígitos do ano atual
        val currentMonth = currentDate.get(Calendar.MONTH) + 1 // Janeiro é 0, por isso somamos 1

        val (month, year) = expiryDate.split("/").map { it.toInt() }

        return if (year > currentYear) {
            true
        } else year == currentYear && month >= currentMonth
    }

    // Função para obter a data e hora atual no formato desejado
    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}