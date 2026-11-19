package com.junco.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.junco.campusgps.model.Lugar
import com.junco.campusgps.model.LugarConDistancia
import com.junco.campusgps.model.UbicacionUi

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    masCercano: Lugar?,
    radioMetros: Int,
    modifier: Modifier = Modifier
) {
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-12.0440, -76.9525), 12f)
    }

    // Cuando llega la ubicación, la cámara viaja a mi posición
    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            val miPosicion = LatLng(estado.posicion.latitud, estado.posicion.longitud)
            camara.animate(CameraUpdateFactory.newLatLngZoom(miPosicion, 16f))
        }
    }

    // Cuando se toca un lugar de la lista, la cámara viaja hasta él
    LaunchedEffect(seleccionado) {
        val lugar = lugares.find { it.lugar.id == seleccionado }?.lugar
        if (lugar != null) {
            camara.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(lugar.latitud, lugar.longitud), 17f)
            )
        }
    }

    GoogleMap(modifier = modifier, cameraPositionState = camara) {
        if (estado is UbicacionUi.Lista) {
            val miPosicion = LatLng(estado.posicion.latitud, estado.posicion.longitud)

            Marker(
                state = rememberUpdatedMarkerState(position = miPosicion),
                title = "Estás aquí"
            )

            // Círculo que representa el radio de cercanía alrededor de mi ubicación
            // (con "Sin radio" no se dibuja)
            if (radioMetros > 0) {
                Circle(
                    center = miPosicion,
                    radius = radioMetros.toDouble(),
                    fillColor = Color(0x333949AB), // Índigo semitransparente
                    strokeColor = Color(0xFF3949AB), // Borde índigo
                    strokeWidth = 2f
                )
            }
        }

        // Un marcador por cada lugar: verde el más cercano, azul los demás
        lugares.forEach { item ->
            val color = if (item.lugar.id == masCercano?.id) {
                BitmapDescriptorFactory.HUE_GREEN
            } else {
                BitmapDescriptorFactory.HUE_AZURE
            }
            key(item.lugar.id) {
                Marker(
                    state = rememberUpdatedMarkerState(
                        position = LatLng(item.lugar.latitud, item.lugar.longitud)
                    ),
                    title = item.lugar.nombre,
                    snippet = item.lugar.categoria,
                    icon = BitmapDescriptorFactory.defaultMarker(color)
                )
            }
        }
    }
}
