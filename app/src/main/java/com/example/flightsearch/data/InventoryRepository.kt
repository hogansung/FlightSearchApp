package com.example.flightsearch.data

import com.example.flightsearch.model.AirportInfo
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao
) {
    fun searchAirportWithSearchInput(searchInput: String): Flow<List<AirportInfo>> =
        airportDao.searchAirportWithSearchInput(searchInput)

    fun listDestinationAirportWithDepartureCode(iataCode: String): Flow<List<AirportInfo>> =
        favoriteDao.listDestinationAirportWithDepartureCode(iataCode)
}