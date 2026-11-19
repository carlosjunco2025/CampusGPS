package com.junco.campusgps.util

import android.location.Location
import com.junco.campusgps.model.Lugar
import com.junco.campusgps.model.Posicion

// Devuelve la distancia en metros desde mi posición hasta un lugar
fun distanciaMetros(desde: Posicion, hasta: Lugar): Float {
    val resultado = FloatArray(1)
    Location.distanceBetween(
        desde.latitud, desde.longitud,
        hasta.latitud, hasta.longitud,
        resultado
    )
    return resultado[0]
}
