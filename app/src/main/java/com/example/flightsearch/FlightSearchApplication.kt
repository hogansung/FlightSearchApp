package com.example.flightsearch

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightsearch.data.FlightSearchPreferencesRepository
import com.example.flightsearch.data.InventoryDatabase
import com.example.flightsearch.data.InventoryDatabaseRepository

class FlightSearchApplication : Application() {
    private val inventoryDatabase: InventoryDatabase by lazy { InventoryDatabase.getDatabase(this) }
    val inventoryDatabaseRepository by lazy {
        InventoryDatabaseRepository(inventoryDatabase.airportDao(), inventoryDatabase.favoriteDao())
    }

    private val Context.dataStore by preferencesDataStore(name = "settings")
    val flightSearchPreferencesRepository by lazy { FlightSearchPreferencesRepository(this.dataStore) }
}