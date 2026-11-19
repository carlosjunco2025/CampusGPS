package com.junco.campusgps.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.junco.campusgps.model.UbicacionUi
import com.junco.campusgps.util.formatearDistancia
import com.junco.campusgps.viewmodel.MapaViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsState()
    val categoriaSeleccionada by vm.categoriaSeleccionada.collectAsState()
    val radioMetros by vm.radioMetros.collectAsState()
    val lugaresEnRadio by vm.lugaresEnRadio.collectAsState()
    val siguiendo by vm.siguiendo.collectAsState()
    val ultimoVisitado by vm.ultimoVisitado.collectAsState()

    val contexto = LocalContext.current

    // Estado de Compose para rastrear si el usuario concedió el permiso de ubicación
    var tienePermiso by remember { mutableStateOf(tienePermisoUbicacion(contexto)) }

    // Recuerda si el permiso se pidió desde el botón Iniciar seguimiento
    var seguirTrasPermiso by remember { mutableStateOf(false) }

    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        val concedido = resultado.values.any { it }
        tienePermiso = concedido
        if (concedido) {
            if (seguirTrasPermiso) vm.iniciarSeguimiento() else vm.buscar()
        } else {
            vm.permisoDenegado()
        }
    }

    fun alPulsar() {
        val concedido = tienePermisoUbicacion(contexto)
        tienePermiso = concedido
        seguirTrasPermiso = false

        if (concedido) {
            vm.buscar()
        } else {
            lanzador.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    fun alPulsarSeguimiento() {
        if (siguiendo) {
            vm.detenerSeguimiento()
            return
        }
        val concedido = tienePermisoUbicacion(contexto)
        tienePermiso = concedido
        seguirTrasPermiso = true

        if (concedido) {
            vm.iniciarSeguimiento()
        } else {
            lanzador.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CampusGPS") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón principal y botón de seguimiento en vivo en la misma fila
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { alPulsar() },
                    // Mientras sigo en vivo la ubicación ya se actualiza sola
                    enabled = estado !is UbicacionUi.Buscando && !siguiendo
                ) {
                    Text("Obtener mi ubicación")
                }

                OutlinedButton(onClick = { alPulsarSeguimiento() }) {
                    Text(if (siguiendo) "Detener" else "Iniciar seguimiento")
                }
            }

            if (siguiendo) {
                Text(
                    text = "Siguiendo tu ubicación en vivo…",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            when (val e = estado) {
                is UbicacionUi.Inicial -> Text("Pulsa el botón para ver dónde estás")
                is UbicacionUi.Buscando -> CircularProgressIndicator()
                is UbicacionUi.Lista -> {
                    // Latitud y longitud en una sola línea para dejar más espacio al mapa
                    Text(
                        text = String.format(
                            Locale.US, "Latitud: %.6f   Longitud: %.6f",
                            e.posicion.latitud, e.posicion.longitud
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                is UbicacionUi.Error -> MensajeError(error = e, onReintentar = { alPulsar() })
            }

            // Último lugar visitado, recuperado de DataStore al abrir la app
            val ultimo = ultimoVisitado
            if (ultimo != null) {
                Text(
                    text = "Último lugar visitado: ${ultimo.nombre}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Primera fila: chips de filtro por categoría (con su propio scroll)
            FiltroCategorias(
                categoriaSeleccionada = categoriaSeleccionada,
                onCategoriaSeleccionada = { categoria -> vm.filtrar(categoria) }
            )

            // Segunda fila: selector del radio de cercanía, también con scroll horizontal
            SelectorRadio(
                radioActual = radioMetros,
                // Sin ubicación no hay círculo ni distancias, así que el selector no hace nada
                habilitado = estado is UbicacionUi.Lista,
                onRadioSeleccionado = { metros -> vm.cambiarRadio(metros) },
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            )

            // El mapa ocupa todo el espacio libre
            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                masCercano = masCercano,
                radioMetros = radioMetros,
                tienePermiso = tienePermiso,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // La lista va debajo con altura fija
            ListaLugares(
                lugares = lugares,
                seleccionado = seleccionado,
                masCercanoId = masCercano?.id,
                lugaresEnRadio = lugaresEnRadio,
                onClick = { id -> vm.seleccionar(id) },
                modifier = Modifier
                    .fillMaxWidth()
                    // Más baja que en la Fase 1 para que con las dos filas de chips el mapa se siga viendo
                    .height(160.dp)
            )

            // Botón para ir al más cercano y, al lado, el texto con su distancia
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { masCercano?.let { vm.seleccionar(it.id) } },
                    enabled = masCercano != null
                ) {
                    Text("Ir al más cercano")
                }

                val cercano = masCercano
                if (cercano != null) {
                    val metros = lugares.find { it.lugar.id == cercano.id }?.metros
                    val texto = if (metros != null) formatearDistancia(metros) else ""
                    Text(
                        text = "Más cercano: ${cercano.nombre} ($texto)",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Revisa si la app ya tiene permiso de ubicación (FINE o COARSE).
// Está en un solo lugar para no repetir el mismo código en la pantalla.
private fun tienePermisoUbicacion(contexto: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        contexto, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(
        contexto, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
}
