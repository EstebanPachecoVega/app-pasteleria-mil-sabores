package com.example.app_pasteleria_mil_sabores.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.app_pasteleria_mil_sabores.model.Direccion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class LocationViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _currentAddress = MutableStateFlow<Direccion?>(null)
    val currentAddress: StateFlow<Direccion?> = _currentAddress.asStateFlow()

    // Para controlar si ya hemos intentado obtener la ubicación
    private var locationAttempted = false

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, onSuccess: (Direccion?) -> Unit) {
        // Resetear el estado de error al iniciar un nuevo intento
        _errorMessage.value = null

        // Verificar si el GPS está activado
        if (!isGPSEnabled(context)) {
            _errorMessage.value = "El GPS no está activado. Por favor, activa la ubicación y vuelve a intentar."
            _isLoading.value = false
            onSuccess(null)
            return
        }

        _isLoading.value = true
        locationAttempted = true

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    viewModelScope.launch {
                        val address = getAddressFromCoordinates(
                            context,
                            location.latitude,
                            location.longitude
                        )
                        _currentAddress.value = address
                        _isLoading.value = false
                        onSuccess(address)
                    }
                } else {
                    // Si lastLocation es null, intentar con requestLocationUpdates
                    requestFreshLocation(fusedLocationClient, context, onSuccess)
                }
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = "Error al obtener ubicación: ${exception.message}"
                _isLoading.value = false
                onSuccess(null)
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation(
        fusedLocationClient: FusedLocationProviderClient,
        context: Context,
        onSuccess: (Direccion?) -> Unit
    ) {
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
            numUpdates = 1
        }

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                val location = locationResult.lastLocation
                if (location != null) {
                    viewModelScope.launch {
                        val address = getAddressFromCoordinates(
                            context,
                            location.latitude,
                            location.longitude
                        )
                        _currentAddress.value = address
                        _isLoading.value = false
                        onSuccess(address)
                    }
                } else {
                    _errorMessage.value = "No se pudo obtener una ubicación precisa. Intenta moverte a un lugar con mejor señal."
                    _isLoading.value = false
                    onSuccess(null)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            .addOnFailureListener { exception ->
                _errorMessage.value = "Error al solicitar ubicación actualizada: ${exception.message}"
                _isLoading.value = false
                onSuccess(null)
            }
    }

    // Función para verificar si el GPS está activado
    private fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // Función para obtener la dirección desde coordenadas
    private suspend fun getAddressFromCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double
    ): Direccion? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                Direccion(
                    calle = address.thoroughfare ?: address.featureName ?: "Calle no identificada",
                    numero = address.subThoroughfare ?: "S/N",
                    departamento = null,
                    comuna = address.locality ?: address.subLocality ?: "Comuna no identificada",
                    ciudad = address.locality ?: address.adminArea ?: "Ciudad no identificada",
                    region = address.adminArea ?: "Región no identificada",
                    codigoPostal = address.postalCode ?: "",
                    coordenadas = com.example.app_pasteleria_mil_sabores.model.Coordenadas(latitude, longitude),
                    instruccionesEspeciales = null
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("LocationViewModel", "Error en Geocoder: ${e.message}")
            null
        }
    }

    // Función para resetear el estado y permitir nuevos intentos
    fun resetLocationState() {
        _isLoading.value = false
        _errorMessage.value = null
        _currentAddress.value = null
        locationAttempted = false
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Función para verificar si ya se intentó obtener la ubicación
    fun hasLocationAttempted(): Boolean = locationAttempted
}