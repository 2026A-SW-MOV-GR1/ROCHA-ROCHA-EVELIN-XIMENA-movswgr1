package com.example.proyectoredseguridad.ui.storage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoredseguridad.storage.DataStoreManager
import com.example.proyectoredseguridad.storage.EncryptedPreferencesManager
import com.example.proyectoredseguridad.storage.SharedPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StorageMechanism { SHARED_PREFS, DATA_STORE, ENCRYPTED_PREFS }

class StorageViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefsManager = SharedPreferencesManager(application)
    private val dataStoreManager = DataStoreManager(application)
    private val encryptedPrefsManager = EncryptedPreferencesManager(application)

    private val _result = MutableStateFlow<String>("—")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun save(mechanism: StorageMechanism, key: String, value: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (mechanism) {
                    StorageMechanism.SHARED_PREFS -> sharedPrefsManager.save(key, value)
                    StorageMechanism.DATA_STORE -> dataStoreManager.save(key, value)
                    StorageMechanism.ENCRYPTED_PREFS -> encryptedPrefsManager.save(key, value)
                }
                _result.value = "Guardado en ${mechanism.label()}\nClave: \"$key\"\nValor: \"$value\""
            } catch (e: Exception) {
                _result.value = "Error al guardar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retrieve(mechanism: StorageMechanism, key: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val value = when (mechanism) {
                    StorageMechanism.SHARED_PREFS -> sharedPrefsManager.retrieve(key)
                    StorageMechanism.DATA_STORE -> dataStoreManager.retrieve(key)
                    StorageMechanism.ENCRYPTED_PREFS -> encryptedPrefsManager.retrieve(key)
                }
                _result.value = if (value != null) {
                    "Recuperado de ${mechanism.label()}\nClave: \"$key\"\nValor: \"$value\""
                } else {
                    "No se encontró ningún valor para la clave \"$key\" en ${mechanism.label()}"
                }
            } catch (e: Exception) {
                _result.value = "Error al recuperar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun StorageMechanism.label() = when (this) {
        StorageMechanism.SHARED_PREFS -> "SharedPreferences"
        StorageMechanism.DATA_STORE -> "DataStore"
        StorageMechanism.ENCRYPTED_PREFS -> "EncryptedSharedPreferences"
    }
}
