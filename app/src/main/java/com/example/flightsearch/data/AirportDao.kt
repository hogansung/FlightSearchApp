package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.model.AirportInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT iata_code, name FROM airport WHERE name LIKE '%' || :searchInput || '%' OR iata_code LIKE '%' || :searchInput || '%'")
    fun searchAirportWithSearchInput(searchInput: String): Flow<List<AirportInfo>>
}