package br.edu.puccampinas.pi4es2024time4.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.puccampinas.pi4es2024time4.databinding.ActivityLoginBinding
import br.edu.puccampinas.pi4es2024time4.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var email: String
    private lateinit var password: String

    //Firebase
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeClickEvents()
        //firebaseAuth.signOut()

    }

    override fun onStart() {
        super.onStart()
        checkLoggedInUser()
    }

    private fun checkLoggedInUser() {

        val currentUser = firebaseAuth.currentUser
        if(currentUser != null){
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    private fun initializeClickEvents() {
        binding.textRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        binding.btnLogin.setOnClickListener {
            if (validateFields()) {
                loginUser()
            }
        }

    }

    private fun loginUser() {

        firebaseAuth.signInWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            showMessage("Logado com sucesso!")
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }.addOnFailureListener { error ->

            try {
                throw error
            } catch (invalidUserError: FirebaseAuthInvalidUserException) {
                invalidUserError.printStackTrace()
                showMessage("E-mail não cadastrado")
            } catch (invalidCredentialsError: FirebaseAuthInvalidCredentialsException) {
                invalidCredentialsError.printStackTrace()
                showMessage("E-mail ou senha estão incorretos")
            }

        }

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
                binding.textInputLayoutLoginPassword.error = "Preencha o e-mail"
                return false
            }

        } else {
            binding.textInputLayoutLoginEmail.error = "Preencha o e-mail"
            return false
        }
    }
}