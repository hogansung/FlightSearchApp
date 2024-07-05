package com.example.flightsearch.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class AirportInfo(
    @ColumnInfo(name = "iata_code") val iataCode: String,
    @ColumnInfo(name = "name") val name: String,
)

data class AirportRouteInfo(
    @Embedded(prefix = "departure_") val departureAirport: AirportInfo,
    @Embedded(prefix = "destination_") val destinationAirport: AirportInfo,
)

data class AirportRouteInfoWithIsFavorite(
    val airportRouteInfo: AirportRouteInfo,
    val isFavorite: Boolean
)
