package br.edu.puccampinas.pi4es2024time4.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast

fun Context.showMessage(message: String){
    Toast.makeText(
    this,
        message,
        Toast.LENGTH_LONG
    ).show()
}