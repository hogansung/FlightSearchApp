package com.example.flightsearch.data

import com.example.flightsearch.model.AirportInfo
import com.example.flightsearch.model.AirportInfoPair
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao
) {
    fun searchAirportWithSearchInput(searchInput: String): Flow<List<AirportInfo>> {
        return airportDao.searchAirportWithSearchInput(searchInput)
    }

    fun listAllAirports(): Flow<List<AirportInfo>> {
        return airportDao.searchAirportWithSearchInput("")
    }

    fun listAllFavoriteRoutes(): Flow<List<AirportInfoPair>> {
        return favoriteDao.listAllFavoriteRoutes()
    }

    suspend fun insertFavoriteRoute(airportInfoPair: AirportInfoPair) {
        favoriteDao.insertFavoriteRoute(
            Favorite(
                departureCode = airportInfoPair.departureAirport.iataCode,
                destinationCode = airportInfoPair.destinationAirport.iataCode
            )
        )
    }

    suspend fun deleteFavoriteRoute(airportInfoPair: AirportInfoPair) {
        favoriteDao.deleteFavoriteRoute(
            departureCode = airportInfoPair.departureAirport.iataCode,
            destinationCode = airportInfoPair.destinationAirport.iataCode
        )
    }
}