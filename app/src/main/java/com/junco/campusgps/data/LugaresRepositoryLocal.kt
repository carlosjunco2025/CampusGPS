package com.junco.campusgps.data

import com.junco.campusgps.model.Lugar

class LugaresRepositoryLocal : LugaresRepository {

    override fun obtenerLugares(): List<Lugar> {
        return listOf(
            // Lugares del campus de TECSUP (Av. Cascanueces 2221, Santa Anita)
            Lugar(1, "Biblioteca Tecsup", "Estudio", -12.044980, -76.952777),
            Lugar(2, "Cafetería Tecsup", "Comida", -12.044947, -76.952453),
            Lugar(3, "Pabellón 11", "Estudio", -12.043656, -76.952464),
            Lugar(4, "Auditorio Tecsup", "Eventos", -12.044801, -76.952714),
            Lugar(5, "Polideportivo Tecsup", "Servicios", -12.042567, -76.952469)
        )
    }
}
