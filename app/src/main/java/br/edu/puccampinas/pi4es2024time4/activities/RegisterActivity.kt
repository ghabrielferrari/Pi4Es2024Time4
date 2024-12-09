package br.edu.puccampinas.pi4es2024time4.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.pi4es2024time4.databinding.ActivityRegisterBinding
import br.edu.puccampinas.pi4es2024time4.model.User
import br.edu.puccampinas.pi4es2024time4.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate( layoutInflater )
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var password: String

    //Firebase
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        initializeToolbar()
        initializeClickEvents()

    }

    private fun initializeClickEvents() {
        binding.btnRegister.setOnClickListener {
            if( validateFields() ){
                registerUser(nome, email, password)
            }
        }
    }

    private fun registerUser(nome: String, email: String, password: String) {

        firebaseAuth.createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { result ->
            if( result.isSuccessful ){

                val idUser = result.result.user?.uid
                if( idUser != null ){
                    val user = User(
                        idUser, nome, email
                    )
                    saveUserFirestore( user )
                }
            }
        }.addOnFailureListener { error ->
            try {
                throw error
            }catch ( weakPasswordError: FirebaseAuthWeakPasswordException ){
                weakPasswordError.printStackTrace()
                showMessage("Senha fraca, digite outra com letras, número e caracteres especiais")
            }catch ( existingUserError: FirebaseAuthUserCollisionException ){
                existingUserError.printStackTrace()
                showMessage("E-mail já percente a outro usuário")
            }catch ( invalidCredentialsError: FirebaseAuthInvalidCredentialsException ){
                invalidCredentialsError.printStackTrace()
                showMessage("E-mail inválido, digite um outro e-mail")
            }
        }

    }

    private fun saveUserFirestore(user: User) {

        firestore
            .collection("usuarios")
            .document( user.id )
            .set( user )
            .addOnSuccessListener {
                showMessage("Sucesso ao fazer seu cadastro")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener {
                showMessage("Erro ao fazer seu cadastro")
            }

    }

    private fun validateFields(): Boolean {

        nome = binding.editName.text.toString()
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()

        if( nome.isNotEmpty() ){

            binding.textInputName.error = null
            if( email.isNotEmpty() ){

                binding.textInputEmail.error = null
                if( password.isNotEmpty() ){
                    binding.textInputPassword.error = null
                    return true
                }else{
                    binding.textInputPassword.error = "Preencha a senha"
                    return false
                }

            }else{
                binding.textInputEmail.error = "Preencha o seu e-mail!"
                return false
            }

        }else{
            binding.textInputName.error = "Preencha o seu nome!"
            return false
        }

    }

    private fun initializeToolbar() {
        val toolbar = binding.includeToolbar.tbMain
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}