package br.edu.puccampinas.pi4es2024time4.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.edu.puccampinas.pi4es2024time4.fragments.ContactsFragment
import br.edu.puccampinas.pi4es2024time4.fragments.ConversationsFragment

class ViewPagerAdapter(
    private val tabs: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            1 -> return ContactsFragment()
        }
        return ConversationsFragment()
    }
}