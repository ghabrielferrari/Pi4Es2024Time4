package br.edu.puccampinas.pi4es2024time4.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import br.edu.puccampinas.pi4es2024time4.R
import br.edu.puccampinas.pi4es2024time4.databinding.FragmentHomeBinding
import br.edu.puccampinas.pi4es2024time4.databinding.FragmentProfileSettingsBinding
import br.edu.puccampinas.pi4es2024time4.model.HomeViewModel
import br.edu.puccampinas.pi4es2024time4.model.ProfileSettingsViewModel

class ProfileSettingsFragment : Fragment() {

    private var _binding: FragmentProfileSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileSettingsViewModel =
            ViewModelProvider(this).get(ProfileSettingsViewModel::class.java)

        _binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProfileSettings
        profileSettingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}