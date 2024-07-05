package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.flightsearch.model.AirportRouteInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query(
        """
        SELECT
            departure_airport.iata_code AS departure_iata_code,
            departure_airport.name AS departure_name,
            destination_airport.iata_code AS destination_iata_code,
            destination_airport.name AS destination_name
        FROM
            favorite
        JOIN
            airport AS departure_airport ON departure_airport.iata_code = favorite.departure_code
        JOIN
            airport AS destination_airport ON destination_airport.iata_code = favorite.destination_code
    """
    )
    fun listAllFavoriteAirportRoutes(): Flow<List<AirportRouteInfo>>

    @Insert
    suspend fun insertFavoriteAirportRoute(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteFavoriteAirportRoute(departureCode: String, destinationCode: String)
}