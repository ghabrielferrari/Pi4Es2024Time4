package br.edu.puccampinas.pi4es2024time4.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

class Conversation(
    val senderUserId: String = "",
    val recipientUserId: String = "",
    val picture: String = "",
    val name: String = "",
    val lastMessage: String = "",
    @ServerTimestamp
    val data: Date? = null
)
