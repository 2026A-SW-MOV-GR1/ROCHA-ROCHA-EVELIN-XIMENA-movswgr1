package com.example.contadorkotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.contadorkotlin.ui.theme.ContadorKotlinTheme

private const val TAG = "ContadorLifecycle"
private const val KEY_COUNT = "contador_key"

class MainActivity : ComponentActivity() {

    // mutableStateOf hace que Compose recomponga automáticamente cuando cambia
    private var count by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, ">>> onCreate | recreada=${savedInstanceState != null}")

        // Si hay estado guardado (ej. rotación), se restaura aquí antes de setContent
        if (savedInstanceState != null) {
            count = savedInstanceState.getInt(KEY_COUNT, 0)
            Log.d(TAG, ">>> onCreate - Estado restaurado desde Bundle. Contador: $count")
        }

        enableEdgeToEdge()
        setContent {
            ContadorKotlinTheme {
                CounterScreen(
                    count = count,
                    onIncrement = {
                        count++
                        Log.d(TAG, "Botón +1 presionado. Contador: $count")
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, ">>> onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, ">>> onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, ">>> onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, ">>> onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, ">>> onDestroy | isFinishing=$isFinishing")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, ">>> onRestart")
    }

    // Se llama ANTES de que la actividad se destruya (ej. rotación)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_COUNT, count)
        Log.d(TAG, ">>> onSaveInstanceState - Guardando contador: $count")
    }

    // Se llama DESPUÉS de onStart cuando la actividad se recrea
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        count = savedInstanceState.getInt(KEY_COUNT, 0)
        Log.d(TAG, ">>> onRestoreInstanceState - Contador recuperado: $count")
    }
}

@Composable
fun CounterScreen(count: Int, onIncrement: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Contador de Taller",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "$count",
                fontSize = 80.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onIncrement,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = "+1",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
