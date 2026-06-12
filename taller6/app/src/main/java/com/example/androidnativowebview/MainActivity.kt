package com.example.androidnativowebview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    // Guardamos datos entrantes mientras el WebView aún no terminó de cargar
    private var pendingTexto: String? = null
    private var pendingImagen: String? = null
    private var paginaCargada = false

    companion object {
        private const val REQUEST_CAMARA = 1001
        private const val PERMISO_CAMARA = 2001
    }

    // ──────────────────────────────────────────────────────────────
    // Ciclo de vida
    // ──────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        configurarWebView()

        // Procesar el Intent que lanzó la app (puede ser un ACTION_SEND entrante)
        handleIntent(intent)
    }

    /**
     * Se invoca cuando la app ya está activa (launchMode="singleTop") y llega
     * un nuevo Intent, por ejemplo al compartir desde otra app.
     * Sin esto, los Intents entrantes se perderían o crearían instancias duplicadas.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)        // actualiza getIntent() para futuras consultas
        handleIntent(intent)
    }

    // ──────────────────────────────────────────────────────────────
    // Configuración del WebView
    // ──────────────────────────────────────────────────────────────

    private fun configurarWebView() {
        webView.settings.apply {
            javaScriptEnabled = true   // obligatorio para que JS llame al Bridge
            domStorageEnabled = true   // permite localStorage en el HTML
        }

        webView.webViewClient = object : WebViewClient() {
            /**
             * Cuando la página termine de cargarse inyectamos cualquier dato
             * que haya llegado antes de que el HTML estuviese listo.
             */
            override fun onPageFinished(view: WebView, url: String) {
                paginaCargada = true

                pendingTexto?.let { texto ->
                    view.evaluateJavascript("mostrarTexto('${escaparJS(texto)}')", null)
                    pendingTexto = null
                }
                pendingImagen?.let { base64 ->
                    view.evaluateJavascript("mostrarImagen('$base64')", null)
                    pendingImagen = null
                }
            }
        }

        webView.webChromeClient = WebChromeClient()

        // Registrar el puente JS ↔ Kotlin con el nombre "AndroidBridge"
        webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")

        // Cargar la UI desde assets/index.html
        webView.loadUrl("file:///android_asset/index.html")
    }

    // ──────────────────────────────────────────────────────────────
    // Módulo 2 – Manejo de Intents entrantes
    // ──────────────────────────────────────────────────────────────

    private fun handleIntent(intent: Intent?) {
        if (intent?.action != Intent.ACTION_SEND) return
        val tipo = intent.type ?: return

        when {
            tipo == "text/plain" -> {
                val texto = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return
                inyectarOGuardarTexto(texto)
            }
            tipo.startsWith("image/") -> {
                // El URI de la imagen viene en EXTRA_STREAM
                val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(Intent.EXTRA_STREAM)
                }
                uri?.let { inyectarOGuardarImagen(it) }
            }
        }
    }

    /** Inyecta el texto al WebView o lo guarda si la página aún no cargó. */
    private fun inyectarOGuardarTexto(texto: String) {
        if (paginaCargada) {
            runOnUiThread {
                webView.evaluateJavascript("mostrarTexto('${escaparJS(texto)}')", null)
            }
        } else {
            pendingTexto = texto
        }
    }

    /**
     * Decodifica el URI en un hilo secundario (evita bloquear la UI),
     * convierte a Base64 y luego inyecta al WebView en el hilo principal.
     */
    private fun inyectarOGuardarImagen(uri: Uri) {
        Thread {
            val base64 = uriABase64(uri) ?: return@Thread
            if (paginaCargada) {
                runOnUiThread {
                    webView.evaluateJavascript("mostrarImagen('$base64')", null)
                }
            } else {
                pendingImagen = base64
            }
        }.start()
    }

    // ──────────────────────────────────────────────────────────────
    // Módulo 1 – Cámara (resultado de startActivityForResult)
    // ──────────────────────────────────────────────────────────────

    /**
     * Captura el thumbnail que devuelve ACTION_IMAGE_CAPTURE (extra "data"),
     * lo convierte a Base64 y lo inyecta al WebView para mostrarlo en el HTML.
     */
    @Deprecated("Usar ActivityResultContracts en código nuevo; mantenido por requerimiento del taller")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAMARA && resultCode == RESULT_OK) {
            val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.getParcelableExtra("data", Bitmap::class.java)
            } else {
                @Suppress("DEPRECATION")
                data?.getParcelableExtra("data")
            }
            bitmap?.let { bmp ->
                val base64 = bitmapABase64(bmp)
                // onActivityResult ya corre en el hilo principal
                webView.evaluateJavascript("mostrarFoto('$base64')", null)
            }
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Permiso de cámara en runtime
    // ──────────────────────────────────────────────────────────────

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISO_CAMARA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lanzarCamara()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun lanzarCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            @Suppress("DEPRECATION")
            startActivityForResult(intent, REQUEST_CAMARA)
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "No se encontró aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Utilidades
    // ──────────────────────────────────────────────────────────────

    /** Convierte un content URI a Base64 usando ImageDecoder (API 28+). */
    private fun uriABase64(uri: Uri): String? {
        return try {
            val source = ImageDecoder.createSource(contentResolver, uri)
            // ALLOCATOR_SOFTWARE asegura un bitmap comprimible en APIs < 31
            val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
            bitmapABase64(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Comprime un Bitmap a JPEG (80 %) y devuelve su representación Base64. */
    private fun bitmapABase64(bitmap: Bitmap): String {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Escapa caracteres especiales de JS para que el string pueda incrustarse
     * con seguridad dentro de evaluateJavascript("f('...')", null).
     */
    private fun escaparJS(texto: String): String =
        texto
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\r", "")

    // ──────────────────────────────────────────────────────────────
    // JavascriptInterface – puente JS → Kotlin
    // ──────────────────────────────────────────────────────────────

    /**
     * El HTML accede a este objeto como `AndroidBridge`.
     * Cada método anotado con @JavascriptInterface puede llamarse desde JS.
     * IMPORTANTE: los métodos se ejecutan en un hilo secundario, por eso
     * las operaciones de UI se envuelven con runOnUiThread.
     */
    inner class AndroidBridge {

        /**
         * Llamado desde JS: AndroidBridge.marcarNumero("0987654321")
         * Lanza el marcador nativo con el número indicado (ACTION_DIAL).
         */
        @JavascriptInterface
        fun marcarNumero(numero: String) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero"))
            startActivity(intent)
        }

        /**
         * Llamado desde JS: AndroidBridge.abrirCamara()
         * Verifica permiso CAMERA y lanza MediaStore.ACTION_IMAGE_CAPTURE.
         */
        @JavascriptInterface
        fun abrirCamara() {
            runOnUiThread {
                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    lanzarCamara()
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        PERMISO_CAMARA
                    )
                }
            }
        }
    }
}
