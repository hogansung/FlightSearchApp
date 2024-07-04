package com.example.flightsearch.model

import androidx.room.ColumnInfo

data class AirportInfo(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "iata_code") val iataCode: String,
)
