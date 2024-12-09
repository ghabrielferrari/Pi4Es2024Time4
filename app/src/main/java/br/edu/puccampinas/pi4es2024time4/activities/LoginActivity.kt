package br.edu.puccampinas.pi4es2024time4.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.pi4es2024time4.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var email: String
    private lateinit var password: String

    // OkHttpClient
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeClickEvents()
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInUser()
    }

    private fun checkLoggedInUser() {
        // Aqui você pode checar se o usuário está logado localmente no Firebase
        // ou se há algum outro tipo de verificação, mas no servidor você vai validar a autenticação.
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
        val email = binding.editLoginEmail.text.toString()
        val password = binding.editLoginPassword.text.toString()

        // Cria o corpo da requisição para o servidor
        val requestBody = FormBody.Builder()
            .add("username", email)
            .add("password", password)
            .build()

        // Cria a requisição POST para o servidor
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/login")
            .post(requestBody)
            .build()

        // Faz a requisição ao servidor
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        // Verifica a resposta do servidor
                        val responseBody = response.body?.string()
                        if (responseBody == "Login realizado com sucesso.") {
                            // Agora que o login foi validado no servidor, autentica o usuário no Firebase
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this@LoginActivity) { task ->
                                    if (task.isSuccessful) {
                                        // Autenticação bem-sucedida com Firebase
                                        val currentUser = FirebaseAuth.getInstance().currentUser
                                        Toast.makeText(this@LoginActivity, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()

                                        // Agora que o usuário está autenticado com o Firebase, inicia a MainActivity
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                        finish() // Finaliza a LoginActivity para que o usuário não possa voltar
                                    } else {
                                        // Se a autenticação com Firebase falhar
                                        Toast.makeText(this@LoginActivity, "Erro de autenticação no Firebase: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            // Caso haja erro no login
                            Toast.makeText(this@LoginActivity, "Erro no login: $responseBody", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Erro no servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    private fun validateFields(): Boolean {
        email = binding.editLoginEmail.text.toString()
        password = binding.editLoginPassword.text.toString()

        if (email.isNotEmpty()) {
            binding.textInputLayoutLoginEmail.error = null
            if (password.isNotEmpty()) {
                binding.textInputLayoutLoginPassword.error = null
                return true
            } else {
                binding.textInputLayoutLoginPassword.error = "Preencha a senha"
                return false
            }
        } else {
            binding.textInputLayoutLoginEmail.error = "Preencha o e-mail"
            return false
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
