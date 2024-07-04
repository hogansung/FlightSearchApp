package com.example.flightsearch

import android.app.Application
import com.example.flightsearch.data.InventoryDatabase
import com.example.flightsearch.data.InventoryRepository

class FlightSearchApplication : Application() {
    private val inventoryDatabase: InventoryDatabase by lazy { InventoryDatabase.getDatabase(this) }
    val inventoryRepository by lazy {
        InventoryRepository(inventoryDatabase.airportDao(), inventoryDatabase.favoriteDao())
    }
}