package com.junco.campusgps.data

import com.junco.campusgps.model.Posicion
import kotlinx.coroutines.flow.Flow

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>
    fun playServicesDisponible(): Boolean

    // Va mandando mi posición cada vez que cambia (seguimiento en vivo)
    fun seguirPosicion(): Flow<Posicion>
}
