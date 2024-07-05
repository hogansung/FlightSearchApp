package com.example.flightsearch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.model.AirportInfo
import com.example.flightsearch.model.AirportInfoPair
import com.example.flightsearch.ui.FlightSearchViewModel
import com.example.flightsearch.ui.theme.FlightSearchTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlightSearchTheme {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    flightSearchViewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.Factory),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flight Search") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            val searchText by flightSearchViewModel.searchText.collectAsState()

            SearchBar(
                searchText = searchText,
                onSearchTextChange = flightSearchViewModel::onSearchTextChange,
                modifier = Modifier.fillMaxWidth()
            )
            if (searchText.isNotEmpty()) {
                val departureAirportInfo by flightSearchViewModel.departureAirportInfo.collectAsState()
                val searchResults by flightSearchViewModel.searchResults.collectAsState()

                if (departureAirportInfo == null) {
                    SearchAutoCompletion(
                        airportInfoList = searchResults,
                        onAirportInfoClick = flightSearchViewModel::onAirportInfoClick
                    )
                } else {
                    val destinationAirportInfoList by flightSearchViewModel.destinationAirportInfoList.collectAsState()

                    FlightList(
                        title = "Flights from ${departureAirportInfo!!.iataCode}",
                        airportInfoPairs = destinationAirportInfoList.map { destinationAirportInfo ->
                            AirportInfoPair(departureAirportInfo!!, destinationAirportInfo)
                        },
                        isFavorite = false,
                        onFavoriteRouteClick = flightSearchViewModel::onFavoriteRouteMarked
                    )
                }
            } else {
                val allFavoriteRoutes by flightSearchViewModel.allFavoriteRoutes.collectAsState()

                FlightList(
                    title = "Favorite Routes",
                    airportInfoPairs = allFavoriteRoutes,
                    isFavorite = true,
                    onFavoriteRouteClick = flightSearchViewModel::onFavoriteRouteUnmarked
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                onSearchTextChange(result?.get(0) ?: "No speech detected.")
            } else {
                onSearchTextChange("[Speech recognition failed.]")
            }
        }
    OutlinedTextField(
        value = TextFieldValue(
            text = searchText,
            selection = TextRange(searchText.length)
        ),
        onValueChange = { value -> onSearchTextChange(value.text) },
        placeholder = {
            Text(
                text = "Search with airport name or IATA code"
            )
        },
        leadingIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = modifier.size(22.dp)
                )
            }
        },
        trailingIcon = {
            if (searchText.isNotBlank()) {
                IconButton(onClick = { onSearchTextChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = modifier.size(22.dp)
                    )
                }
            } else {
                IconButton(onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Go on then, say something.")
                    launcher.launch(intent)
                }) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = modifier.size(22.dp)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun SearchAutoCompletion(
    airportInfoList: List<AirportInfo>,
    onAirportInfoClick: (AirportInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn {
        items(items = airportInfoList) { searchResult ->
            AirportInfoRow(
                airportInfo = searchResult,
                modifier = modifier
                    .padding(start = 20.dp, top = 5.dp, end = 20.dp)
                    .clickable(onClick = { onAirportInfoClick(searchResult) })
            )
        }
    }
}

@Composable
fun AirportInfoRow(
    airportInfo: AirportInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(text = airportInfo.iataCode, fontSize = 13.sp, fontWeight = FontWeight(1000))
        Text(text = airportInfo.name, fontSize = 13.sp)
    }
}

@Composable
fun FlightList(
    title: String,
    airportInfoPairs: List<AirportInfoPair>,
    isFavorite: Boolean,
    onFavoriteRouteClick: (AirportInfoPair) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.padding(10.dp)) {
        Text(text = title, fontWeight = FontWeight(1000))
        Spacer(modifier = Modifier.padding(10.dp))
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items = airportInfoPairs) { airportInfoPair ->
                FlightRow(
                    airportInfoPair = airportInfoPair,
                    isFavorite = isFavorite,
                    onFavoriteRouteClick = onFavoriteRouteClick,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun FlightRow(
    airportInfoPair: AirportInfoPair,
    isFavorite: Boolean,
    onFavoriteRouteClick: (AirportInfoPair) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
            Column {
                Text(text = "DEPART", fontSize = 11.sp, modifier = modifier)
                AirportInfoRow(
                    airportInfo = airportInfoPair.departureAirport,
                    modifier = modifier
                )
                Spacer(modifier.height(5.dp))
                Text(text = "ARRIVE", fontSize = 11.sp, modifier = modifier)
                AirportInfoRow(
                    airportInfo = airportInfoPair.destinationAirport,
                    modifier = modifier
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                onFavoriteRouteClick(airportInfoPair)
            }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = modifier.size(22.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FlightSearchTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    FlightSearchTheme {
        SearchBar("123", {})
    }
}