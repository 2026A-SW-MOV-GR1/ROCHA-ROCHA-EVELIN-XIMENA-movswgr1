package com.example.dualpersistenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dualpersistenceapp.data.model.Item
import com.example.dualpersistenceapp.data.repository.ItemRepository
import com.example.dualpersistenceapp.util.AppLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StorageMode { SQL, NOSQL }

class ItemViewModel(
    private val roomRepo: ItemRepository,
    private val dataStoreRepo: ItemRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _storageMode = MutableStateFlow(StorageMode.SQL)
    val storageMode: StateFlow<StorageMode> = _storageMode.asStateFlow()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val currentRepo get() = if (_storageMode.value == StorageMode.SQL) roomRepo else dataStoreRepo

    init {
        loadItems()
    }

    fun toggleStorageMode() {
        _storageMode.value = if (_storageMode.value == StorageMode.SQL) StorageMode.NOSQL else StorageMode.SQL
        AppLogger.info("ItemViewModel", "toggleStorageMode -> ${_storageMode.value}")
        loadItems()
    }

    fun addItem(title: String, description: String) {
        viewModelScope.launch(ioDispatcher) {
            val item = Item(title = title, description = description, createdAt = System.currentTimeMillis())
            AppLogger.info("ItemViewModel", "addItem: $title")
            currentRepo.insert(item)
            loadItems()
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch(ioDispatcher) {
            AppLogger.info("ItemViewModel", "updateItem: ${item.title}")
            currentRepo.update(item)
            loadItems()
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch(ioDispatcher) {
            AppLogger.info("ItemViewModel", "deleteItem: ${item.title}")
            currentRepo.delete(item)
            loadItems()
        }
    }

    private fun loadItems() {
        viewModelScope.launch(ioDispatcher) {
            AppLogger.debug("ItemViewModel", "loadItems from ${_storageMode.value}")
            _items.value = currentRepo.getAll()
        }
    }

    class Factory(
        private val roomRepo: ItemRepository,
        private val dataStoreRepo: ItemRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ItemViewModel(roomRepo, dataStoreRepo) as T
    }
}
