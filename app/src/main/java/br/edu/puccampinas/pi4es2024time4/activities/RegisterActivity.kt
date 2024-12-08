package br.edu.puccampinas.pi4es2024time4.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.pi4es2024time4.databinding.ActivityRegisterBinding
import br.edu.puccampinas.pi4es2024time4.model.RegisterRequest
import br.edu.puccampinas.pi4es2024time4.network.RetrofitInstance
import br.edu.puccampinas.pi4es2024time4.utils.showMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeClickEvents()
    }

    private fun initializeClickEvents() {
        binding.btnRegister.setOnClickListener {
            if (validateFields()) {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        val request = RegisterRequest(
            id = java.util.UUID.randomUUID().toString(),
            email = email,
            senha = password,
            nome = nome,
            cpf = "000.000.000-00" // Exemplo fixo, substitua conforme necessário
        )

        RetrofitInstance.api.register(request).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    showMessage("Usuário registrado com sucesso!")
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                } else {
                    showMessage("Erro no registro: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                showMessage("Erro ao conectar ao servidor: ${t.message}")
            }
        })
    }

    private fun validateFields(): Boolean {
        nome = binding.editName.text.toString()
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()

        if (nome.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            return true
        }
        showMessage("Preencha todos os campos")
        return false
    }
}
