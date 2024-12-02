package br.edu.puccampinas.pi4es2024time4

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.pi4es2024time4.R

class ActivityServico : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servico)

        val serviceName = intent.getStringExtra("service_name")
        val providerName = intent.getStringExtra("provider_name")
        val serviceDescription = intent.getStringExtra("service_description")

        val serviceNameTextView: TextView = findViewById(R.id.serviceNameTextView)
        val providerNameTextView: TextView = findViewById(R.id.providerNameTextView)
        val serviceDescriptionTextView: TextView = findViewById(R.id.serviceDescriptionTextView)

        serviceNameTextView.text = serviceName
        providerNameTextView.text = providerName
        serviceDescriptionTextView.text = serviceDescription
    }
}