package br.edu.puccampinas.pi4es2024time4.busca

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.pi4es2024time4.R
import java.text.SimpleDateFormat
import java.util.*

class DebitCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debit_card)

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
                    Toast.makeText(this, "Pagamento realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    // Aqui você pode adicionar a lógica para processar o pagamento, se necessário
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

        // Valida se a data de validade é maior ou igual a 2025/25
        if (year > currentYear + 1) {
            return true
        } else if (year == currentYear + 1) {
            return month >= currentMonth
        } else {
            return false
        }
    }
}
