package com.junco.campusgps.data

import com.junco.campusgps.model.Lugar

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}
