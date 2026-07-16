package com.example.examen2bmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.examen2bmoviles.models.ActivitySelection
import com.example.examen2bmoviles.models.TouristInfo
import com.example.examen2bmoviles.ui.screens.MapScreen
import com.example.examen2bmoviles.ui.theme.Examen2BMovilesTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val touristInfo = extractTouristInfoFromIntent(intent)
        val isDemoMode = !intent.hasExtra("id_turista")

        setContent {
            Examen2BMovilesTheme {
                MapScreen(
                    touristInfo = touristInfo,
                    isDemoMode = isDemoMode,
                    onSendToApp3 = { selection -> sendToApp3(selection) }
                )
            }
        }
    }

    private fun extractTouristInfoFromIntent(intent: Intent): TouristInfo {
        return TouristInfo(
            idTurista = intent.getStringExtra("id_turista") ?: "turista_demo_001",
            nombreRestaurante = intent.getStringExtra("nombre_restaurante") ?: "Restaurante El Quiteño",
            latitudRestaurante = intent.getDoubleExtra("latitud_restaurante", -0.2201),
            longitudRestaurante = intent.getDoubleExtra("longitud_restaurante", -78.5124),
            horaReserva = intent.getStringExtra("hora_reserva") ?: "13:00"
        )
    }

    private fun sendToApp3(selection: ActivitySelection) {
        // Intent hacia App 3 (Andreina - Movilidad / Transporte)
        val intent = Intent("com.turismoquito.ACTIVIDADES_TO_TRANSPORTE").apply {
            // Datos acumulados de App 1 (Gastronomía)
            putExtra("id_turista", selection.touristInfo.idTurista)
            putExtra("nombre_restaurante", selection.touristInfo.nombreRestaurante)
            putExtra("latitud_restaurante", selection.touristInfo.latitudRestaurante)
            putExtra("longitud_restaurante", selection.touristInfo.longitudRestaurante)
            putExtra("hora_reserva", selection.touristInfo.horaReserva)
            // Datos de App 2 (esta app - Actividades)
            putExtra("nombre_actividad", selection.actividad.nombre)
            putExtra("latitud_encuentro", selection.actividad.latitud)
            putExtra("longitud_encuentro", selection.actividad.longitud)
            putExtra("costo_actividad", selection.costoFinal)
        }

        try {
            startActivity(intent)
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(
                this,
                "App 3 (Transporte) aún no instalada. Datos listos: ${selection.actividad.nombre} · $${selection.costoFinal}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
