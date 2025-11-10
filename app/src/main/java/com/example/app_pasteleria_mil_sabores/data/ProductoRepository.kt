package com.example.app_pasteleria_mil_sabores.data

import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.utils.IdGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ProductoRepository(private val productoDao: ProductoDao) {

    // Cargar datos iniciales solo si la base de datos está vacía
    suspend fun inicializarDatos() {
        val productosExistentes = productoDao.obtenerTodos().firstOrNull()
        if (productosExistentes.isNullOrEmpty()) {
            productoDao.insertarTodos(obtenerProductosIniciales())
        }
    }

    private fun obtenerProductosIniciales(): List<Producto> {
        return listOf(
            // POSTRES INDIVIDUALES
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Mousse de Chocolate",
                descripcion = "Postre individual cremoso y suave, hecho con chocolate de alta calidad, ideal para los amantes del chocolate.",
                precio = 5000,
                imagen = "mousse_de_chocolate",
                categoria = "individuales",
                stock = 15,
                destacado = true
            ),
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Tiramisú Clásico",
                descripcion = "Un postre italiano individual con capas de café, mascarpone y cacao, perfecto para finalizar cualquier comida.",
                precio = 5500,
                imagen = "tiramisu_clasico",
                categoria = "individuales",
                stock = 12,
                destacado = false
            ),

            // SIN AZÚCAR
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Torta Sin Azúcar de Naranja",
                descripcion = "Torta ligera y deliciosa, endulzada naturalmente, ideal para quienes buscan opciones más saludables.",
                precio = 48000,
                imagen = "torta_sin_azucar_de_naranja",
                categoria = "sin_azucar",
                stock = 8,
                destacado = true
            ),
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Cheesecake Sin Azúcar",
                descripcion = "Suave y cremoso, este cheesecake es una opción perfecta para disfrutar sin culpa.",
                precio = 47000,
                imagen = "cheesecake_sin_azucar",
                categoria = "sin_azucar",
                stock = 1,
                destacado = false
            ),

            // SIN GLUTEN
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Brownie Sin Gluten",
                descripcion = "Rico y denso, este brownie es perfecto para quienes necesitan evitar el gluten sin sacrificar el sabor.",
                precio = 4000,
                imagen = "brownie_sin_gluten",
                categoria = "sin_gluten",
                stock = 20,
                destacado = true
            ),
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Pan Sin Gluten",
                descripcion = "Suave y esponjoso, ideal para sándwiches o para acompañar cualquier comida.",
                precio = 3500,
                imagen = "pan_sin_gluten",
                categoria = "sin_gluten",
                stock = 25,
                destacado = false
            ),

            // VEGANOS
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Torta Vegana de Chocolate",
                descripcion = "Torta de chocolate húmeda y deliciosa, hecha sin productos de origen animal, perfecta para veganos.",
                precio = 50000,
                imagen = "torta_vegana_de_chocolate",
                categoria = "veganos",
                stock = 5,
                destacado = true
            ),
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Galletas Veganas de Avena",
                descripcion = "Crujientes y sabrosas, estas galletas son una excelente opción para un snack saludable y vegano.",
                precio = 4500,
                imagen = "galletas_veganas_de_avena",
                categoria = "veganos",
                stock = 30,
                destacado = false
            ),

            // TORTAS CIRCULARES
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Torta Circular de Vainilla",
                descripcion = "Bizcocho de vainilla clásico relleno con crema pastelera y cubierto con un glaseado dulce, perfecto para cualquier ocasión.",
                precio = 40000,
                imagen = "torta_circular_de_vainilla",
                categoria = "circulares",
                stock = 3,
                destacado = true
            ),
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Torta Circular de Manjar",
                descripcion = "Torta tradicional chilena con manjar y nueces, un deleite para los amantes de los sabores dulces y clásicos.",
                precio = 42000,
                imagen = "torta_circular_de_manjar",
                categoria = "circulares",
                stock = 4,
                destacado = false
            ),

            // TORTAS CUADRADAS
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Torta Cuadrada de Chocolate",
                descripcion = "Deliciosa torta de chocolate con capas de ganache y un toque de avellanas. Personalizable con mensajes especiales.",
                precio = 45000,
                imagen = "torta_cuadrada_de_chocolate",
                categoria = "cuadradas",
                stock = 0,
                destacado = true
            ),
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Torta Cuadrada de Frutas",
                descripcion = "Una mezcla de frutas frescas y crema chantilly sobre un suave bizcocho de vainilla, ideal para celebraciones.",
                precio = 50000,
                imagen = "torta_cuadrada_de_frutas",
                categoria = "cuadradas",
                stock = 3,
                destacado = false
            ),

            // TORTAS ESPECIALES
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Torta Especial de Cumpleaños",
                descripcion = "Diseñada especialmente para celebraciones, personalizable con decoraciones y mensajes únicos.",
                precio = 55000,
                imagen = "torta_especial_de_cumpleanos",
                categoria = "especiales",
                stock = 2,
                destacado = true
            ),
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Torta Especial de Boda",
                descripcion = "Elegante y deliciosa, esta torta está diseñada para ser el centro de atención en cualquier boda.",
                precio = 60000,
                imagen = "torta_especial_de_boda",
                categoria = "especiales",
                stock = 2,
                destacado = false
            ),

            // TRADICIONAL
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Empanadas de Manzana",
                descripcion = "Pastelería tradicional rellena de manzanas especiadas, perfecta para un dulce desayuno o merienda.",
                precio = 3000,
                imagen = "empanadas_de_manzana",
                categoria = "tradicional",
                stock = 18,
                destacado = true
            ),
            Producto(
                id = IdGenerator.generarIdProducto(),
                nombre = "Tarta de Santiago",
                descripcion = "Tradicional tarta española hecha con almendras, azúcar, y huevos, una delicia para los amantes de los postres clásicos.",
                precio = 6000,
                imagen = "tarta_de_santiago",
                categoria = "tradicional",
                stock = 10,
                destacado = false
            )
        )
    }

    // Métodos del DAO expuestos a través del Repository
    suspend fun insertar(producto: Producto) = productoDao.insertar(producto)

    suspend fun insertarTodos(productos: List<Producto>) = productoDao.insertarTodos(productos)

    suspend fun actualizar(producto: Producto) = productoDao.actualizar(producto)

    suspend fun cambiarEstado(id: String, activo: Boolean) = productoDao.cambiarEstado(id, activo)

    fun obtenerTodos(): Flow<List<Producto>> = productoDao.obtenerTodos()

    suspend fun obtenerPorId(id: String): Producto? = productoDao.obtenerPorId(id)

    fun obtenerPorCategoria(categoria: String): Flow<List<Producto>> = productoDao.obtenerPorCategoria(categoria)

    fun obtenerDestacados(): Flow<List<Producto>> = productoDao.obtenerDestacados()

    fun buscar(query: String): Flow<List<Producto>> = productoDao.buscar(query)

    fun obtenerCategorias(): Flow<List<String>> = productoDao.obtenerCategorias()

    suspend fun descontarStock(productoId: String, cantidad: Int): Boolean {
        return productoDao.descontarStock(productoId, cantidad) > 0
    }

    suspend fun obtenerStock(productoId: String): Int? {
        return productoDao.obtenerStock(productoId)
    }

    suspend fun actualizarStock(productoId: String, nuevoStock: Int) {
        productoDao.actualizarStock(productoId, nuevoStock)
    }

    suspend fun verificarStockSuficiente(productoId: String, cantidadRequerida: Int): Boolean {
        val stockActual = obtenerStock(productoId)
        return stockActual != null && stockActual >= cantidadRequerida
    }
}