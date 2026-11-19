package com.junco.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.junco.campusgps.data.LugaresRepositoryLocal
import com.junco.campusgps.data.PreferenciasRepositoryDataStore
import com.junco.campusgps.data.UbicacionRepositoryGoogle

class CampusViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapaViewModel(
            UbicacionRepositoryGoogle(context.applicationContext),
            LugaresRepositoryLocal(),
            PreferenciasRepositoryDataStore(context.applicationContext)
        ) as T
    }
}
