package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.InventoryRepository
import com.example.flightsearch.model.AirportInfo
import com.example.flightsearch.model.AirportRouteInfo
import com.example.flightsearch.model.AirportRouteInfoWithIsFavorite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FlightSearchViewModel(private val repository: InventoryRepository) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _searchResults = MutableStateFlow<List<AirportInfo>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _departureAirportInfo = MutableStateFlow<AirportInfo?>(null)
    val departureAirportInfo = _departureAirportInfo.asStateFlow()

    private val _destinationAirportInfoList = MutableStateFlow<List<AirportInfo>>(emptyList())
    val destinationAirportInfoList = _destinationAirportInfoList.asStateFlow()

    private val _favoriteAirportRouteInfoList =
        MutableStateFlow<List<AirportRouteInfo>>(emptyList())
    val favoriteAirportRouteInfoList = _favoriteAirportRouteInfoList.asStateFlow()

    fun onSearchTextChange(searchText: String) {
        _searchText.value = searchText

        viewModelScope.launch {
            repository.searchAirportWithSearchInput(searchText).collect { airflowInfoList ->
                _searchResults.value = airflowInfoList
            }
        }

        // When `searchText` is cleared, so is the `departureAirportInfo`
        if (searchText.isEmpty()) {
            _departureAirportInfo.value = null
        }
    }

    fun onAirportInfoClick(airportInfo: AirportInfo) {
        _departureAirportInfo.value = airportInfo
        viewModelScope.launch {
            repository.listAllAirports()
                .collect { airportInfoList ->
                    _destinationAirportInfoList.value = airportInfoList
                }
        }
    }

    private fun listAllFavoriteAirportRoutes() {
        viewModelScope.launch {
            repository.listAllFavoriteAirportRoutes().collect { favoriteAirportRouteInfoList ->
                _favoriteAirportRouteInfoList.value = favoriteAirportRouteInfoList
            }
        }
    }

    fun onAirportRouteInfoWithIsFavoriteClick(airportRouteInfoWithIsFavorite: AirportRouteInfoWithIsFavorite) {
        if (airportRouteInfoWithIsFavorite.isFavorite) {
            viewModelScope.launch {
                repository.deleteFavoriteRoute(airportRouteInfoWithIsFavorite = airportRouteInfoWithIsFavorite)
            }
        } else {
            viewModelScope.launch {
                repository.insertFavoriteRoute(airportRouteInfoWithIsFavorite = airportRouteInfoWithIsFavorite)
            }
        }

        listAllFavoriteAirportRoutes()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FlightSearchViewModel(
                    (this[APPLICATION_KEY] as FlightSearchApplication).inventoryRepository
                )
            }
        }
    }
}