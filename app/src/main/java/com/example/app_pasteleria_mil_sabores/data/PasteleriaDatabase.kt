package com.example.app_pasteleria_mil_sabores.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.model.Usuario

@Database(entities = [Usuario::class, Producto::class], version = 1)
abstract class PasteleriaDatabase : RoomDatabase(){
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
}