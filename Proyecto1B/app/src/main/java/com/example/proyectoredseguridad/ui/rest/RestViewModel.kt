package com.example.proyectoredseguridad.ui.rest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoredseguridad.network.RetrofitClient
import com.example.proyectoredseguridad.network.model.Post
import kotlinx.coroutines.launch

class RestViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> = _post

    private val _statusMessage = MutableLiveData("Ingresa un ID y pulsa GET")
    val statusMessage: LiveData<String> = _statusMessage

    fun getPost(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Cargando..."
            try {
                val response = RetrofitClient.apiService.getPost(id)
                if (response.isSuccessful) {
                    _post.value = response.body()
                    _statusMessage.value = "Post $id obtenido exitosamente"
                } else {
                    _statusMessage.value = "Error HTTP ${response.code()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePost(id: Int, title: String, body: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Actualizando..."
            try {
                val updatedPost = Post(
                    userId = _post.value?.userId ?: 1,
                    id = id,
                    title = title,
                    body = body
                )
                val response = RetrofitClient.apiService.updatePost(id, updatedPost)
                if (response.isSuccessful) {
                    _post.value = response.body()
                    _statusMessage.value = "Post $id actualizado exitosamente"
                } else {
                    _statusMessage.value = "Error HTTP ${response.code()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
