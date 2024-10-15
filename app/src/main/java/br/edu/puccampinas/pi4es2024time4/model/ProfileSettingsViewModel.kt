package br.edu.puccampinas.pi4es2024time4.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileSettingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Profile Settings home Fragment"
    }
    val text: LiveData<String> = _text
}