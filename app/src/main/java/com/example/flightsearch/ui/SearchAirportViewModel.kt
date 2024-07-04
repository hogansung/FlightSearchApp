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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SearchAirportViewModel(private val repository: InventoryRepository) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _searchAutoCompletes = MutableStateFlow<List<AirportInfo>>(emptyList())
    val searchAutoCompletes = _searchAutoCompletes.asStateFlow()

    fun onSearchTextChange(searchText: String) {
        _searchText.value = searchText
        searchAirportWithSearchText()
    }

    fun searchAirportWithSearchText() {
        viewModelScope.launch {
            repository.searchAirportWithSearchInput(searchText.value).collect { airflowInfoList ->
                _searchAutoCompletes.value = airflowInfoList
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchAirportViewModel(
                    (this[APPLICATION_KEY] as FlightSearchApplication).inventoryRepository
                )
            }
        }
    }
}