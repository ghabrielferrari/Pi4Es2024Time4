package br.edu.puccampinas.pi4es2024time4.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String= "",
    var name: String= "",
    var email: String= "",
    var picture: String = ""
): Parcelable
