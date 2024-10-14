package br.edu.puccampinas.pi4es2024time4.utils

import android.app.Activity
import android.widget.Toast

fun Activity.exibirMensagem(message: String){
    Toast.makeText(
    this,
        message,
        Toast.LENGTH_LONG
    ).show()
}