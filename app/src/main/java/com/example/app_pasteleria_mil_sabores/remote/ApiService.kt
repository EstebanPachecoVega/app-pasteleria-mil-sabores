package com.example.app_pasteleria_mil_sabores.remote

import com.example.app_pasteleria_mil_sabores.model.Receta
import retrofit2.http.GET


interface ApiService {
    @GET("recetas")
    suspend fun getRecetas(): List<Receta>
}