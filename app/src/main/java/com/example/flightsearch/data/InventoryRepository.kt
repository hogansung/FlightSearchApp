package com.example.flightsearch.data

import com.example.flightsearch.model.AirportInfo
import com.example.flightsearch.model.AirportRouteInfo
import com.example.flightsearch.model.AirportRouteInfoWithIsFavorite
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

    fun listAllFavoriteAirportRoutes(): Flow<List<AirportRouteInfo>> {
        return favoriteDao.listAllFavoriteAirportRoutes()
    }

    suspend fun insertFavoriteRoute(airportRouteInfoWithIsFavorite: AirportRouteInfoWithIsFavorite) {
        favoriteDao.insertFavoriteAirportRoute(
            Favorite(
                departureCode = airportRouteInfoWithIsFavorite.airportRouteInfo.departureAirport.iataCode,
                destinationCode = airportRouteInfoWithIsFavorite.airportRouteInfo.destinationAirport.iataCode
            )
        )
    }

    suspend fun deleteFavoriteRoute(airportRouteInfoWithIsFavorite: AirportRouteInfoWithIsFavorite) {
        favoriteDao.deleteFavoriteAirportRoute(
            departureCode = airportRouteInfoWithIsFavorite.airportRouteInfo.departureAirport.iataCode,
            destinationCode = airportRouteInfoWithIsFavorite.airportRouteInfo.destinationAirport.iataCode
        )
    }
}