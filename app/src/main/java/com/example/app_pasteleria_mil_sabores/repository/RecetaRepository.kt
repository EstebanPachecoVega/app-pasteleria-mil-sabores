package com.example.app_pasteleria_mil_sabores.repository

import com.example.app_pasteleria_mil_sabores.model.Receta
import com.example.app_pasteleria_mil_sabores.model.RecetaBasic
import com.example.app_pasteleria_mil_sabores.model.RecetaResponse
import com.example.app_pasteleria_mil_sabores.model.RecetasResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RecetaApiService {
    @GET("filter.php")
    suspend fun getPostres(@Query("c") category: String = "Dessert"): RecetasResponse

    @GET("lookup.php")
    suspend fun getRecetaById(@Query("i") id: String): RecetaResponse
}

class RecetaRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(RecetaApiService::class.java)

    suspend fun getPostres(): List<RecetaBasic> {
        return try {
            val response = apiService.getPostres()
            response.meals
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecetaById(id: String): Receta? {
        return try {
            val response = apiService.getRecetaById(id)
            response.meals.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
}