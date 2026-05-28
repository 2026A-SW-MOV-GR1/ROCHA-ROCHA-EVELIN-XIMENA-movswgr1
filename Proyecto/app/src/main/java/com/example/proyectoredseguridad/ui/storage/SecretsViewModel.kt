package com.example.proyectoredseguridad.ui.storage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.proyectoredseguridad.storage.DataStoreManager
import com.example.proyectoredseguridad.storage.EncryptedPreferencesManager
import com.example.proyectoredseguridad.storage.SharedPreferencesManager
import kotlinx.coroutines.launch

enum class Mecanismo {
    SHARED_PREFERENCES,
    DATA_STORE,
    ENCRYPTED_SHARED_PREFERENCES
}

class SecretsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefsManager = SharedPreferencesManager(application)
    private val dataStoreManager = DataStoreManager(application)
    private val encryptedPrefsManager = EncryptedPreferencesManager(application)

    private val _resultado = MutableLiveData("—")
    val resultado: LiveData<String> = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun guardar(mecanismo: Mecanismo, llave: String, valor: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (mecanismo) {
                    Mecanismo.SHARED_PREFERENCES -> sharedPrefsManager.save(llave, valor)
                    Mecanismo.DATA_STORE -> dataStoreManager.save(llave, valor)
                    Mecanismo.ENCRYPTED_SHARED_PREFERENCES -> encryptedPrefsManager.save(llave, valor)
                }
                _resultado.value = "Guardado en ${mecanismo.label()}\nLlave: \"$llave\"\nValor: \"$valor\""
            } catch (e: Exception) {
                _resultado.value = "Error al guardar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun recuperar(mecanismo: Mecanismo, llave: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val valor = when (mecanismo) {
                    Mecanismo.SHARED_PREFERENCES -> sharedPrefsManager.retrieve(llave)
                    Mecanismo.DATA_STORE -> dataStoreManager.retrieve(llave)
                    Mecanismo.ENCRYPTED_SHARED_PREFERENCES -> encryptedPrefsManager.retrieve(llave)
                }
                _resultado.value = if (valor != null) {
                    "Recuperado de ${mecanismo.label()}\nLlave: \"$llave\"\nValor: \"$valor\""
                } else {
                    "No encontrado para \"$llave\" en ${mecanismo.label()}"
                }
            } catch (e: Exception) {
                _resultado.value = "Error al recuperar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun Mecanismo.label() = when (this) {
        Mecanismo.SHARED_PREFERENCES -> "SharedPreferences"
        Mecanismo.DATA_STORE -> "DataStore"
        Mecanismo.ENCRYPTED_SHARED_PREFERENCES -> "EncryptedSharedPreferences"
    }
}
