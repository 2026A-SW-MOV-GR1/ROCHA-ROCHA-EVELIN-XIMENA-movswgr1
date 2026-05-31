package com.example.dualpersistenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dualpersistenceapp.data.local.room.AppDatabase
import com.example.dualpersistenceapp.data.repository.DataStoreItemRepository
import com.example.dualpersistenceapp.data.repository.RoomItemRepository
import com.example.dualpersistenceapp.ui.screens.ItemFormScreen
import com.example.dualpersistenceapp.ui.screens.ItemListScreen
import com.example.dualpersistenceapp.ui.theme.DualPersistenceAppTheme
import com.example.dualpersistenceapp.ui.viewmodel.ItemViewModel

class MainActivity : ComponentActivity() {

    private val roomRepo by lazy {
        RoomItemRepository(AppDatabase.getDatabase(this).itemDao())
    }

    private val dataStoreRepo by lazy {
        DataStoreItemRepository(this)
    }

    private val viewModel: ItemViewModel by viewModels {
        ItemViewModel.Factory(roomRepo, dataStoreRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DualPersistenceAppTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
private fun AppNavigation(viewModel: ItemViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            ItemListScreen(
                viewModel = viewModel,
                onNavigateToForm = { navController.navigate("form") }
            )
        }
        composable("form") {
            ItemFormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
