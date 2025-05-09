package br.edu.puccampinas.pi4es2024time4.busca

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.pi4es2024time4.R

class PaymentMethodActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        // Referência para os botões
        val buttonCreditCard: Button = findViewById(R.id.buttonCredit)
        val buttonDebit: Button = findViewById(R.id.buttonDebit)
        val buttonPix: Button = findViewById(R.id.buttonPix)

        buttonCreditCard.setOnClickListener {
            val intent = Intent(this, CreditCardActivity::class.java)
            startActivity(intent)
        }

        // Ao clicar no botão Pix, navega para a PixActivity
        buttonPix.setOnClickListener {
            val intent = Intent(this, PixActivity::class.java)
            startActivity(intent)
        }

        // Ao clicar no botão Débito, navega para a DebitActivity (ajuste o nome conforme sua Activity)
        buttonDebit.setOnClickListener {
            val intent = Intent(this, DebitCardActivity::class.java)
            startActivity(intent)
        }
    }
}
