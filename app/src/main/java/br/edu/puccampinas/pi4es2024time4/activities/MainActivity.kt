package br.edu.puccampinas.pi4es2024time4.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.pi4es2024time4.ForunsDuvidaActivity
import br.edu.puccampinas.pi4es2024time4.R
import br.edu.puccampinas.projeto.Service
import br.edu.puccampinas.projeto.ServiceAdapter
import br.edu.puccampinas.pi4es2024time4.adapters.ViewPagerAdapter
import br.edu.puccampinas.pi4es2024time4.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // Firebase
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var firestore: FirebaseFirestore

    // RecyclerView
    private lateinit var serviceList: MutableList<Service>
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeToolbar()
        initializeTabNavigation()
        initializeRecyclerView()
        loadServices()

        // Botão para o chatBot
        val buttonNext: Button = findViewById(R.id.buttonNext)
        buttonNext.setOnClickListener {
            startActivity(Intent(this, ChatBotActivity::class.java))
        }

        // Botão para o Forum
        val buttonForum: Button = findViewById(R.id.buttonForum)
        buttonForum.setOnClickListener {
            startActivity(Intent(this, ForunsDuvidaActivity::class.java))
        }
    }

    private fun initializeTabNavigation() {
        val tabLayout = binding.tabLayoutMain
        val viewPager = binding.viewPagerMain

        val tabs = listOf("CONVERSAS", "CONTATOS")
        viewPager.adapter = ViewPagerAdapter(tabs, supportFragmentManager, lifecycle)

        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun initializeToolbar() {
        val toolbar = binding.includeMainToolbar.tbMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Auxilia"
        }

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.item_profile -> {
                            startActivity(
                                Intent(applicationContext, ProfileActivity::class.java)
                            )
                        }

                        R.id.item_logout -> {
                            logoutUser()
                        }
                    }
                    return true
                }
            }
        )
    }

    private fun initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        serviceList = mutableListOf()
        serviceAdapter = ServiceAdapter(this, serviceList) { /* Implementar ação de clique */ }
        recyclerView.adapter = serviceAdapter

        firestore = FirebaseFirestore.getInstance()
    }

    private fun loadServices() {
        firestore.collection("serviços")
            .get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                serviceList.clear()
                if (!snapshot.isEmpty) {
                    for (document in snapshot) {
                        val service = document.toObject(Service::class.java)
                        service?.let {
                            serviceList.add(it)
                        }
                    }
                    serviceAdapter.notifyDataSetChanged()
                } else {
                    Log.d("Firestore", "Nenhum dado encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Erro ao buscar dados: ${e.message}")
            }
    }

    private fun logoutUser() {
        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Cancelar") { _, _ -> }
            .setPositiveButton("Sim") { _, _ ->
                firebaseAuth.signOut()
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java)
                )
            }
            .create()
            .show()
    }
}
