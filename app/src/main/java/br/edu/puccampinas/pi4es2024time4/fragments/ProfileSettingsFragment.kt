package br.edu.puccampinas.pi4es2024time4.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.edu.puccampinas.pi4es2024time4.databinding.FragmentProfileSettingsBinding
import br.edu.puccampinas.pi4es2024time4.model.ProfileSettingsViewModel
import br.edu.puccampinas.pi4es2024time4.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileSettingsFragment : Fragment() {

    private var _binding: FragmentProfileSettingsBinding? = null
    private val binding get() = _binding!!

    private var hasCameraPermission = false
    private var hasGalleryPermission = false

    // Firebase
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val galleryManager = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            binding.imageProfile.setImageURI(uri)
            uploadImageToStorage(uri)
        } else {
            requireContext().showMessage("Nenhuma imagem selecionada")
        }
    }

    private val permissionManager = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasGalleryPermission = permissions[if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }] ?: hasGalleryPermission
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileSettingsViewModel =
            ViewModelProvider(this).get(ProfileSettingsViewModel::class.java)

        _binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSettingsProfile
        profileSettingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        initializeClickEvents()
        retrieveInitialUserData()
        requestPermissions()

        return root
    }

    private fun retrieveInitialUserData() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val userData = documentSnapshot.data
                    if (userData != null) {
                        val name = userData["name"] as String
                        val photo = userData["photo"] as String

                        binding.editProfileName.setText(name)
                        if (photo.isNotEmpty()) {
                            Picasso.get()
                                .load(photo)
                                .into(binding.imageProfile)
                        }
                    }
                }
        }
    }

    private fun uploadImageToStorage(uri: Uri) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            storage
                .getReference("photos")
                .child("users")
                .child(userId)
                .child("profile.jpg")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    requireContext().showMessage("Imagem carregada com sucesso")
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener { uri ->
                            val data = mapOf("photo" to uri.toString())
                            updateProfileData(userId, data)
                        }
                }.addOnFailureListener {
                    requireContext().showMessage("Erro ao carregar imagem")
                }
        }
    }

    private fun updateProfileData(userId: String, data: Map<String, String>) {
        firestore
            .collection("users")
            .document(userId)
            .update(data)
            .addOnSuccessListener {
                requireContext().showMessage("Perfil atualizado com sucesso")
            }
            .addOnFailureListener {
                requireContext().showMessage("Erro ao atualizar perfil")
            }
    }

    private fun initializeClickEvents() {
        binding.fabImageProfile.setOnClickListener {
            checkGalleryPermissionAndLaunch()
        }

        binding.btnUpdateProfile.setOnClickListener {
            val userName = binding.editProfileName.text.toString()
            if (userName.isNotEmpty()) {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    val data = mapOf("name" to userName)
                    updateProfileData(userId, data)
                }
            } else {
                requireContext().showMessage("Por favor, preencha o nome")
            }
        }
    }

    private fun checkGalleryPermissionAndLaunch() {
        if (hasGalleryPermission) {
            galleryManager.launch("image/*")
        } else {
            requireContext().showMessage("Sem permissão para acessar a galeria")
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        hasGalleryPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        ) == PackageManager.PERMISSION_GRANTED

        val deniedPermissions = mutableListOf<String>()
        if (!hasCameraPermission)
            deniedPermissions.add(Manifest.permission.CAMERA)
        if (!hasGalleryPermission)
            deniedPermissions.add(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
            )

        if (deniedPermissions.isNotEmpty()) {
            permissionManager.launch(deniedPermissions.toTypedArray())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
