package br.edu.puccampinas.pi4es2024time4.model

data class LoginResponse(
    val message: String,
    val token: String? = null
)

data class RegisterRequest(
    val id: String,
    val email: String,
    val senha: String,
    val nome: String,
    val cpf: String
)
