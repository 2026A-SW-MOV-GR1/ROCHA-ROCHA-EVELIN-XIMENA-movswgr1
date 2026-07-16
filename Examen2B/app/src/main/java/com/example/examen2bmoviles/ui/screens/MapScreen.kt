package com.example.examen2bmoviles.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.examen2bmoviles.data.QuitoActivities
import com.example.examen2bmoviles.models.ActivitySelection
import com.example.examen2bmoviles.models.QuitoActivity
import com.example.examen2bmoviles.models.TouristInfo
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    touristInfo: TouristInfo,
    isDemoMode: Boolean,
    onSendToApp3: (ActivitySelection) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var selectedActivity by remember { mutableStateOf<QuitoActivity?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var costoText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val mapView = remember {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidTileCache = context.cacheDir
        }
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(13.0)
            controller.setCenter(GeoPoint(-0.2295, -78.5243))
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    // Rebuild markers when tourist info changes
    LaunchedEffect(touristInfo) {
        mapView.overlays.clear()

        // Marker del restaurante recibido de App 1
        val restaurantMarker = Marker(mapView).apply {
            position = GeoPoint(touristInfo.latitudRestaurante, touristInfo.longitudRestaurante)
            title = touristInfo.nombreRestaurante
            snippet = "Reserva: ${touristInfo.horaReserva} · Turista: ${touristInfo.idTurista}"
            icon = createCircleMarker(context.resources, android.graphics.Color.parseColor("#E53935"), 48)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapView.overlays.add(restaurantMarker)

        // Marcadores de actividades turísticas
        QuitoActivities.lista.forEach { activity ->
            val color = categoryColor(activity.categoria)
            val marker = Marker(mapView).apply {
                position = GeoPoint(activity.latitud, activity.longitud)
                title = activity.nombre
                snippet = "${activity.categoria} · Guía: ${activity.guia} · $${activity.costoBase}"
                icon = createCircleMarker(context.resources, color, 44)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                setOnMarkerClickListener { _, _ ->
                    selectedActivity = activity
                    costoText = activity.costoBase.toString()
                    showBottomSheet = true
                    true
                }
            }
            mapView.overlays.add(marker)
        }

        mapView.invalidate()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Guías y Actividades", fontWeight = FontWeight.Bold)
                        Text(
                            "Ecosistema Turismo Quito · App 2",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isDemoMode) {
                DemoBanner()
            }

            TouristInfoCard(touristInfo = touristInfo)

            // Leyenda de colores
            MapLegend()

            // Mapa OSMDroid
            Box(modifier = Modifier.weight(1f)) {
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Panel inferior: actividad seleccionada o instrucción
            selectedActivity?.let { activity ->
                SelectedActivityCard(
                    activity = activity,
                    costo = costoText.toDoubleOrNull() ?: activity.costoBase,
                    onSend = {
                        onSendToApp3(
                            ActivitySelection(
                                touristInfo = touristInfo,
                                actividad = activity,
                                costoFinal = costoText.toDoubleOrNull() ?: activity.costoBase
                            )
                        )
                    }
                )
            } ?: HintCard()
        }
    }

    if (showBottomSheet) {
        selectedActivity?.let { activity ->
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                ActivityDetailSheet(
                    activity = activity,
                    costoText = costoText,
                    onCostoChange = { costoText = it },
                    onConfirm = { showBottomSheet = false }
                )
            }
        }
    }
}

@Composable
private fun DemoBanner() {
    Surface(color = MaterialTheme.colorScheme.tertiaryContainer) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MODO DEMO — App 1 aún no conectada",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TouristInfoCard(touristInfo: TouristInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = "Turista: ${touristInfo.idTurista}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Viene de: ${touristInfo.nombreRestaurante}  ·  Reserva: ${touristInfo.horaReserva}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun MapLegend() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LegendItem(color = Color(0xFFE53935), label = "Restaurante")
                LegendItem(color = Color(0xFF1565C0), label = "Cultural/Histórico")
            }
            Spacer(Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LegendItem(color = Color(0xFF2E7D32), label = "Aventura/Deporte")
                LegendItem(color = Color(0xFF6A1B9A), label = "Arqueología")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(10.dp),
            shape = MaterialTheme.shapes.small,
            color = color
        ) {}
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SelectedActivityCard(
    activity: QuitoActivity,
    costo: Double,
    onSend: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Actividad seleccionada",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = activity.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Guía: ${activity.guia}  ·  Costo: $${"%.2f".format(costo)}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onSend,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Enviar a Transporte (App 3)")
            }
        }
    }
}

@Composable
private fun HintCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = "Toca un marcador de color en el mapa para seleccionar una actividad turística",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ActivityDetailSheet(
    activity: QuitoActivity,
    costoText: String,
    onCostoChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = activity.nombre,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        AssistChip(
            onClick = {},
            label = { Text(activity.categoria) }
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = activity.descripcion,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))

        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(6.dp))
            Column {
                Text("Guía asignado", style = MaterialTheme.typography.labelSmall)
                Text(activity.guia, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Coordenadas: ${"%.4f".format(activity.latitud)}, ${"%.4f".format(activity.longitud)}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = costoText,
            onValueChange = onCostoChange,
            label = { Text("Costo de la actividad ($)") },
            supportingText = { Text("Costo base: $${activity.costoBase}") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar actividad")
        }
        Spacer(Modifier.height(24.dp))
    }
}

// Crea un marcador circular de color para distinguir tipos de punto
private fun createCircleMarker(resources: android.content.res.Resources, color: Int, sizeDp: Int): BitmapDrawable {
    val size = sizeDp * 3
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = size * 0.08f
    }
    val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = android.graphics.Color.WHITE }
    val cx = size / 2f
    val cy = size / 2f
    val radius = size / 2f - 4f
    canvas.drawCircle(cx, cy, radius, fillPaint)
    canvas.drawCircle(cx, cy, radius, borderPaint)
    canvas.drawCircle(cx, cy, radius * 0.3f, centerPaint)
    return BitmapDrawable(resources, bitmap)
}

private fun categoryColor(categoria: String): Int = when (categoria) {
    "Aventura" -> android.graphics.Color.parseColor("#2E7D32")
    "Deporte" -> android.graphics.Color.parseColor("#1B5E20")
    "Cultural" -> android.graphics.Color.parseColor("#1565C0")
    "Histórico" -> android.graphics.Color.parseColor("#0D47A1")
    "Compras" -> android.graphics.Color.parseColor("#E65100")
    "Arqueología" -> android.graphics.Color.parseColor("#6A1B9A")
    else -> android.graphics.Color.parseColor("#37474F")
}
