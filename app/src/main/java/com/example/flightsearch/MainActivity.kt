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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.model.AirportInfo
import com.example.flightsearch.ui.SearchAirportViewModel
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
    searchAirportViewModel: SearchAirportViewModel = viewModel(factory = SearchAirportViewModel.Factory),
) {
    val searchText by searchAirportViewModel.searchText.collectAsState()
    val searchAutoCompletes by searchAirportViewModel.searchAutoCompletes.collectAsState()
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
            SearchBar(
                searchText = searchText,
                onSearchTextChange = searchAirportViewModel::onSearchTextChange,
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(searchText.isNotEmpty()) {
                SearchAutoCompletes(searchAutoCompletes = searchAutoCompletes)
            }
            SearchResults()
            FavoriteFlights()
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
fun SearchAutoCompletes(searchAutoCompletes: List<AirportInfo>) {
    LazyColumn {
        items(items = searchAutoCompletes) { searchAutoComplete ->
            SearchAutoComplete(searchAutoComplete = searchAutoComplete)
        }
    }
}

@Composable
fun SearchAutoComplete(searchAutoComplete: AirportInfo) {
    Text(text = "${searchAutoComplete.iataCode} ${searchAutoComplete.name}")
}

@Composable
fun SearchResults() {

}

@Composable
fun FavoriteFlights() {
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