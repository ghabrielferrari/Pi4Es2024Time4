package br.edu.puccampinas.pi4es2024time4.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import br.edu.puccampinas.pi4es2024time4.databinding.ActivityProfileBinding
import br.edu.puccampinas.pi4es2024time4.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private var hasCameraPermission = false
    private var hasGalleryPermission = false

    // Firebase Instances
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    // Permission and Gallery Launchers
    private lateinit var permissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>
    private lateinit var galleryLauncher: androidx.activity.result.ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeActivityResultLaunchers()
        initializeToolbar()
        requestPermissions()
        initializeClickEvents()
    }

    override fun onStart() {
        super.onStart()
        fetchInitialUserData()
    }

    private fun initializeActivityResultLaunchers() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
            hasGalleryPermission = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: hasGalleryPermission
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                // Redimensiona e exibe a imagem
                resizeImage(uri)?.let { resizedImage ->
                    binding.imageProfile.setImageBitmap(resizedImage)
                    uploadImageToStorage(uri)
                } ?: showMessage("Erro ao redimensionar a imagem")
            } else {
                showMessage("Nenhuma imagem selecionada")
            }
        }
    }

    private fun resizeImage(uri: Uri): Bitmap? {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Verifica se a imagem foi carregada corretamente
        originalBitmap ?: return null

        // Redimensiona a imagem se necessário
        val maxWidth = 800
        val maxHeight = 800
        val scaleFactor = Math.min(originalBitmap.width / maxWidth, originalBitmap.height / maxHeight)

        return Bitmap.createScaledBitmap(originalBitmap, originalBitmap.width / scaleFactor, originalBitmap.height / scaleFactor, false)
    }

    private fun fetchInitialUserData() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firestore.collection("usuarios").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    documentSnapshot.data?.let { userData ->
                        val name = userData["nome"] as? String ?: ""
                        val photoUrl = userData["foto"] as? String ?: ""

                        binding.editProfileName.setText(name)
                        if (photoUrl.isNotEmpty()) {
                            // Redimensiona a imagem ao carregá-la com Picasso
                            Picasso.get()
                                .load(photoUrl)
                                .resize(800, 800) // Redimensiona a imagem
                                .centerCrop() // Para manter a proporção
                                .into(binding.imageProfile)
                        }
                    }
                }
        }
    }

    private fun uploadImageToStorage(uri: Uri) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            storage.getReference("fotos/usuarios/$userId/perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    showMessage("Sucesso ao fazer upload da imagem")
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                        val data = mapOf("foto" to downloadUri.toString())
                        updateUserProfile(userId, data)
                    }
                }.addOnFailureListener {
                    showMessage("Erro ao fazer upload da imagem")
                }
        }
    }

    private fun updateUserProfile(userId: String, data: Map<String, String>) {
        firestore.collection("usuarios").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Atualiza o documento existente
                    firestore.collection("usuarios").document(userId).update(data)
                        .addOnSuccessListener {
                            showMessage("Sucesso ao atualizar perfil")
                        }
                        .addOnFailureListener {
                            showMessage("Erro ao atualizar perfil do usuário")
                        }
                } else {
                    // Se o documento não existir, crie um novo
                    firestore.collection("usuarios").document(userId).set(data)
                        .addOnSuccessListener {
                            showMessage("Perfil criado com sucesso")
                        }
                        .addOnFailureListener {
                            showMessage("Erro ao criar perfil do usuário")
                        }
                }
            }.addOnFailureListener {
                showMessage("Erro ao buscar perfil do usuário")
            }
    }

    private fun initializeClickEvents() {
        binding.fabSelect.setOnClickListener {
            if (hasGalleryPermission) {
                galleryLauncher.launch("image/*")
            } else {
                showMessage("Sem permissão para acessar galeria")
                requestPermissions()
            }
        }

        binding.btnProfileUpdate.setOnClickListener {
            val userName = binding.editProfileName.text.toString()
            if (userName.isNotEmpty()) {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    val data = mapOf("nome" to userName)
                    updateUserProfile(userId, data)
                }
            } else {
                showMessage("Preencha o nome para atualizar")
            }
        }
    }

    private fun requestPermissions() {
        // Verifica permissões atuais
        hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        hasGalleryPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        // Lista permissões negadas
        val deniedPermissions = mutableListOf<String>().apply {
            if (!hasCameraPermission) add(Manifest.permission.CAMERA)
            if (!hasGalleryPermission) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
                add(Manifest.permission.READ_EXTERNAL_STORAGE) // Incluindo permissão legada
            }
        }

        if (deniedPermissions.isNotEmpty()) {
            permissionLauncher.launch(deniedPermissions.toTypedArray())
        } else {
            showMessage("Permissões já concedidas")
        }
    }

    private fun initializeToolbar() {
        val toolbar = binding.includeProfileToolbar.tbMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}