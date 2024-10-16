package br.edu.puccampinas.pi4es2024time4.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.pi4es2024time4.databinding.ActivitySignUpBinding
import br.edu.puccampinas.pi4es2024time4.model.User
import br.edu.puccampinas.pi4es2024time4.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    //Binding
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    //Firebase
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()
    }

    private fun inicializarEventosClique() {
        binding.textSignIn.setOnClickListener {
            startActivity(
                Intent(this, SignInActivity::class.java)
            )
        }

        binding.signUpButton.setOnClickListener {
            if (validarCampos()) {
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String , senha: String) {

        firebaseAuth.createUserWithEmailAndPassword(
            email, senha
        ).addOnCompleteListener { resultado ->
            if (resultado.isSuccessful) {
                val idUsuario = resultado.result.user?.uid
                if (idUsuario != null) {
                    val usuario = User(
                        idUsuario,nome, email
                    )
                    salvarUsuarioFirestore(usuario)
                }
            }
        }.addOnFailureListener { erro ->
            try {
                throw erro
            }catch ( erroSenhaFraca: FirebaseAuthWeakPasswordException){
                erroSenhaFraca.printStackTrace()
                showMessage("Senha fraca, digite outra com letras, número e caracteres especiais")
            }catch ( erroUsuarioExistente: FirebaseAuthUserCollisionException){
                erroUsuarioExistente.printStackTrace()
                showMessage("E-mail já percente a outro usuário")
            }catch ( erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException){
                erroCredenciaisInvalidas.printStackTrace()
                showMessage("E-mail inválido, digite um outro e-mail")
            }
        }
    }

    private fun salvarUsuarioFirestore(usuario: User) {
        firestore
            .collection("usuarios")
            .document( usuario.id )
            .set( usuario )
            .addOnSuccessListener {
                showMessage("Sucesso ao fazer seu cadastro")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener {
                showMessage("Erro ao fazer seu cadastro")
            }
    }

    private fun validarCampos(): Boolean {

        nome = binding.editName.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editPassword.text.toString()

        if( nome.isNotEmpty() ){
            binding.textInputName.error = null

            if( email.isNotEmpty() ){
                binding.textInputEmail.error = null

                if( senha.isNotEmpty() ){
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

}