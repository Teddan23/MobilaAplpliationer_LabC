package com.example.mobilaapplikationerlabc.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.compose.ui.text.input.KeyboardType
import com.example.mobilaapplikationerlabc.api.TheMealDBService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import coil.compose.rememberImagePainter
import com.example.mobilaapplikationerlabc.model.Meal
import com.example.mobilaapplikationerlabc.model.MealResponse

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExploreScreen(navController: NavController) {
    // TextField for searching meal
    var searchQuery by remember { mutableStateOf("") }
    var mealsList by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Setup Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(TheMealDBService::class.java)

    // Function to handle search
    fun searchMeals(query: String) {
        if (query.isEmpty()) return

        isLoading = true
        errorMessage = null
        service.searchMeal(query).enqueue(object : Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                if (response.isSuccessful) {
                    mealsList = response.body()?.meals ?: emptyList()
                } else {
                    errorMessage = "No meals found."
                }
                isLoading = false
            }

            override fun onFailure(call: Call<MealResponse>, t: Throwable) {
                errorMessage = t.message
                isLoading = false
            }
        })
    }

    // UI content
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Screen") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Search TextField
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search for a meal") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            searchMeals(searchQuery)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Show loading or error
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
                } else if (!mealsList.isNullOrEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(mealsList) { meal ->
                            MealItem(
                                meal = meal,
                                navController = navController//TODO: WHAT D: ????
                            )
                        }
                    }
                } else if (errorMessage != null) {
                    Text(text = errorMessage ?: "No results found", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Composable
fun MealItem(meal: Meal, navController: NavController) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                // Navigera till detaljskärmen när användaren klickar på måltiden
                navController.navigate("mealDetail/${meal.idMeal}")
            }
    ) {
        Text(text = meal.strMeal, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = meal.strCategory, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(4.dp))
        meal.strMealThumb?.let {
            Image(painter = rememberImagePainter(it), contentDescription = meal.strMeal)
        }
    }
}
