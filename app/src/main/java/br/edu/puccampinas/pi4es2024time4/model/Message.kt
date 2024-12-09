package br.edu.puccampinas.pi4es2024time4.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val userId: String = "",
    val message: String = "",
    @ServerTimestamp
    val data: Date? = null,
)
