package br.edu.puccampinas.pi4es2024time4.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.pi4es2024time4.databinding.ActivityLoginBinding
import br.edu.puccampinas.pi4es2024time4.model.LoginResponse
import br.edu.puccampinas.pi4es2024time4.network.RetrofitInstance
import br.edu.puccampinas.pi4es2024time4.utils.showMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeClickEvents()
    }

    private fun initializeClickEvents() {
        binding.textRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            if (validateFields()) {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        val request = mapOf("email" to email, "senha" to password)

        RetrofitInstance.api.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.token != null) {
                    showMessage("Login bem-sucedido! Token: ${response.body()?.token}")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    showMessage("Credenciais inválidas")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showMessage("Erro ao conectar ao servidor: ${t.message}")
            }
        })
    }

    private fun validateFields(): Boolean {
        email = binding.editLoginEmail.text.toString()
        password = binding.editLoginPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            return true
        }
        showMessage("Preencha todos os campos")
        return false
    }
}
