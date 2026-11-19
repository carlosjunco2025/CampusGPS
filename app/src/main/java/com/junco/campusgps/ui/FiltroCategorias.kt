package com.junco.campusgps.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Fila de chips para filtrar los lugares por categoría.
// El chip "Todos" quita el filtro (categoría null).
@Composable
fun FiltroCategorias(
    categoriaSeleccionada: String?,
    onCategoriaSeleccionada: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categorias = listOf("Estudio", "Comida", "Eventos", "Servicios")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = categoriaSeleccionada == null,
            onClick = { onCategoriaSeleccionada(null) },
            label = { Text("Todos") }
        )

        categorias.forEach { categoria ->
            FilterChip(
                selected = categoria == categoriaSeleccionada,
                onClick = { onCategoriaSeleccionada(categoria) },
                label = { Text(categoria) }
            )
        }
    }
}
