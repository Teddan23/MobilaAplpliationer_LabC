package com.example.mobilaapplikationerlabc.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.mobilaapplikationerlabc.api.TheMealDBService
import com.example.mobilaapplikationerlabc.api.mapToMeal
import com.example.mobilaapplikationerlabc.model.Meal
import com.example.mobilaapplikationerlabc.model.MealResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomScreen(navController: NavController) {

    // Setup Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(TheMealDBService::class.java)

    // State for holding meal data
    var meal by remember { mutableStateOf<Meal?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch random meal from API
    LaunchedEffect(Unit) {
        service.getRandomMeal().enqueue(object : Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                if (response.isSuccessful) {
                    meal = response.body()?.let { mapToMeal(it) }
                } else {
                    errorMessage = "Failed to load meal."
                }
                isLoading = false
            }

            override fun onFailure(call: Call<MealResponse>, t: Throwable) {
                errorMessage = t.message
                isLoading = false
            }
        })
    }


    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Random Meal") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        // REMOVE the contentPadding parameter completely, we handle padding manually
        content = { paddingValues -> // Use paddingValues directly here
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            } else if (errorMessage != null) {
                Text(text = "Error: $errorMessage", color = Color.Red, modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
            } else {
                meal?.let {
                    RandomMealContent(meal = it, paddingValues = paddingValues)
                }
            }
        }
    )
}

@Composable
fun RandomMealContent(meal: Meal, paddingValues: PaddingValues) {
    // Skapa listan av ingredienser och deras mått
    val ingredientsAndMeasures = listOf(
        meal.strIngredient1 to meal.strMeasure1,
        meal.strIngredient2 to meal.strMeasure2,
        meal.strIngredient3 to meal.strMeasure3,
        meal.strIngredient4 to meal.strMeasure4,
        meal.strIngredient5 to meal.strMeasure5,
        meal.strIngredient6 to meal.strMeasure6,
        meal.strIngredient7 to meal.strMeasure7,
        meal.strIngredient8 to meal.strMeasure8,
        meal.strIngredient9 to meal.strMeasure9,
        meal.strIngredient10 to meal.strMeasure10,
        meal.strIngredient11 to meal.strMeasure11,
        meal.strIngredient12 to meal.strMeasure12,
        meal.strIngredient13 to meal.strMeasure13,
        meal.strIngredient14 to meal.strMeasure14,
        meal.strIngredient15 to meal.strMeasure15,
        meal.strIngredient16 to meal.strMeasure16,
        meal.strIngredient17 to meal.strMeasure17,
        meal.strIngredient18 to meal.strMeasure18,
        meal.strIngredient19 to meal.strMeasure19,
        meal.strIngredient20 to meal.strMeasure20
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Meal Image
        item {
            Image(
                painter = rememberImagePainter(meal.strMealThumb),
                contentDescription = meal.strMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        // Meal Name
        item {
            Text(text = meal.strMeal, style = MaterialTheme.typography.headlineMedium)
        }

        // Category
        item {
            Text(text = "Category: ${meal.strCategory}")
        }

        // Instructions
        item {
            Text(text = "Instructions: ${meal.strInstructions}", modifier = Modifier.padding(8.dp))
        }

        // Ingredients Header
        item {
            Text(text = "Ingredients:", style = MaterialTheme.typography.bodyLarge)
        }

        // Ingredients and Measures
        items(ingredientsAndMeasures) { ingredientAndMeasure ->
            val (ingredient, measure) = ingredientAndMeasure
            ingredient?.takeIf { it.isNotBlank() }?.let { nonNullIngredient ->
                Text(
                    text = "$nonNullIngredient: ${measure?.takeIf { it.isNotBlank() } ?: "No measurement available"}"
                )
            }
        }
    }
}