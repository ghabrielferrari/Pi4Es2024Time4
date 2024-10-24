package br.edu.puccampinas.pi4es2024time4.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import br.edu.puccampinas.pi4es2024time4.R
import br.edu.puccampinas.pi4es2024time4.adapters.ViewPagerAdapter
import br.edu.puccampinas.pi4es2024time4.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    //Firebase
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeToolbar()
        initializeTabNavigation()

    }

    private fun initializeTabNavigation() {

        val tabLayout = binding.tabLayoutMain
        val viewPager = binding.viewPagerMain

        //Adapter
        val tabs = listOf("CONVERSAS", "CONTATOS")
        viewPager.adapter = ViewPagerAdapter(
            tabs, supportFragmentManager, lifecycle
        )


        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPager){tab, position ->
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

    private fun logoutUser() {

        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Cancelar") { dialog, position -> }
            .setPositiveButton("Sim") { dialog, position ->
                firebaseAuth.signOut()
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java)
                )
            }
            .create()
            .show()

    }
}

