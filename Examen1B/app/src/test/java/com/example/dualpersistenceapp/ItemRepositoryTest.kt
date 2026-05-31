package com.example.dualpersistenceapp

import com.example.dualpersistenceapp.data.local.room.ItemDao
import com.example.dualpersistenceapp.data.local.room.ItemEntity
import com.example.dualpersistenceapp.data.model.Item
import com.example.dualpersistenceapp.data.repository.ItemRepository
import com.example.dualpersistenceapp.data.repository.RoomItemRepository
import com.example.dualpersistenceapp.ui.viewmodel.ItemViewModel
import com.example.dualpersistenceapp.ui.viewmodel.StorageMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// ── Fakes ────────────────────────────────────────────────────────────────────

class FakeItemDao : ItemDao {
    private val store = mutableListOf<ItemEntity>()
    private var nextId = 1

    override fun getAllItems(): Flow<List<ItemEntity>> = flowOf(store.toList())

    override fun insert(item: ItemEntity) {
        store.add(item.copy(id = nextId++))
    }

    override fun update(item: ItemEntity) {
        val idx = store.indexOfFirst { it.id == item.id }
        if (idx >= 0) store[idx] = item
    }

    override fun delete(item: ItemEntity) {
        store.removeAll { it.id == item.id }
    }
}

class FakeItemRepository : ItemRepository {
    val store = mutableListOf<Item>()
    private var nextId = 1

    override suspend fun getAll(): List<Item> = store.toList()

    override suspend fun insert(item: Item) {
        store.add(item.copy(id = nextId++))
    }

    override suspend fun update(item: Item) {
        val idx = store.indexOfFirst { it.id == item.id }
        if (idx >= 0) store[idx] = item
    }

    override suspend fun delete(item: Item) {
        store.removeAll { it.id == item.id }
    }
}

// ── Tests ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class ItemRepositoryTest {

    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test 1: inserta un Item en RoomItemRepository con DAO en memoria
     * y verifica con getAll() que existe.
     */
    @Test
    fun test1_roomRepository_insertThenGetAll_itemExists() = runTest(testDispatcher) {
        val fakeDao = FakeItemDao()
        val repository = RoomItemRepository(fakeDao)

        repository.insert(Item(title = "Título Test", description = "Descripción Test", createdAt = 1000L))

        val result = repository.getAll()

        assertEquals(1, result.size)
        assertEquals("Título Test", result[0].title)
        assertEquals("Descripción Test", result[0].description)
    }

    /**
     * Test 2: crea fakes de ambos repositorios, instancia ItemViewModel,
     * agrega un item en modo SQL, llama toggleStorageMode(), verifica que
     * el modo cambió a NOSQL y que items se recargó desde DataStore.
     */
    @Test
    fun test2_viewModel_toggleStorageMode_changesAndReloadsItems() = runTest(testDispatcher) {
        val fakeRoomRepo = FakeItemRepository()
        val fakeDataStoreRepo = FakeItemRepository()

        // Pre-cargar un item en el repo NoSQL para diferenciar las listas
        fakeDataStoreRepo.insert(Item(title = "Item NoSQL", description = "En DataStore", createdAt = 2000L))

        val viewModel = ItemViewModel(fakeRoomRepo, fakeDataStoreRepo, testDispatcher)
        advanceUntilIdle()

        // Verificar modo inicial SQL
        assertEquals(StorageMode.SQL, viewModel.storageMode.value)

        // Agregar item en modo SQL
        viewModel.addItem("Item SQL", "En Room")
        advanceUntilIdle()

        assertEquals(1, viewModel.items.value.size)
        assertEquals("Item SQL", viewModel.items.value[0].title)

        // Cambiar a NoSQL
        viewModel.toggleStorageMode()
        advanceUntilIdle()

        // Verificar que el modo cambió
        assertEquals(StorageMode.NOSQL, viewModel.storageMode.value)

        // Verificar que items se recargó desde DataStore
        assertEquals(1, viewModel.items.value.size)
        assertEquals("Item NoSQL", viewModel.items.value[0].title)
    }
}
