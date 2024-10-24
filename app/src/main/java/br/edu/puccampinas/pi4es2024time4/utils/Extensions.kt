package br.edu.puccampinas.pi4es2024time4.utils

import android.app.Activity
import android.widget.Toast

fun Activity.showMessage(mensagem: String) {
    Toast.makeText(
        this,
        mensagem,
        Toast.LENGTH_LONG
    ).show()
}