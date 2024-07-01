package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.model.AirportInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT iata_code, name FROM favorite JOIN airport ON favorite.destination_code = airport.iata_code WHERE departure_code = :departureCode")
    fun listDestinationAirportWithDepartureCode(departureCode: String): Flow<List<AirportInfo>>
}