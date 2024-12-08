package br.edu.puccampinas.pi4es2024time4.network

import br.edu.puccampinas.pi4es2024time4.model.LoginResponse
import br.edu.puccampinas.pi4es2024time4.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("loginUsuario")
    fun login(@Body request: Map<String, String>): Call<LoginResponse>

    @POST("cadastrarUsuario")
    fun register(@Body request: RegisterRequest): Call<String>
}
