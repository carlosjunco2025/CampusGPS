package com.junco.campusgps.data

import kotlinx.coroutines.flow.Flow

interface PreferenciasRepository {
    // Id del último lugar que el usuario tocó (null si nunca tocó uno)
    val ultimoLugar: Flow<Int?>
    suspend fun guardarUltimoLugar(id: Int)
}
