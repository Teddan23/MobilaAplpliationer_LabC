package com.example.mobilaapplikationerlabc.Screens

import MealViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.mobilaapplikationerlabc.api.TheMealDBService
import com.example.mobilaapplikationerlabc.model.Meal
import com.example.mobilaapplikationerlabc.model.MealResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(mealId: String, navController: NavController,  viewModel: MealViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    // Ladda måltiden baserat på mealId (vi använder Retrofit för att hämta detaljer)
    var meal by remember { mutableStateOf<Meal?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Setup Retrofit (som tidigare)
    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(TheMealDBService::class.java)

    // Hämtar måltiden baserat på mealId
    LaunchedEffect(mealId) {
        service.getMealDetails(mealId).enqueue(object : Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                if (response.isSuccessful) {
                    meal = response.body()?.meals?.firstOrNull()
                } else {
                    errorMessage = "No meal details found."
                }
                isLoading = false
            }

            override fun onFailure(call: Call<MealResponse>, t: Throwable) {
                errorMessage = t.message
                isLoading = false
            }
        })
    }

    // UI content for MealDetailScreen
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp), // Extra padding för innehållet
                verticalArrangement = Arrangement.spacedBy(16.dp) // Lägger till avstånd mellan items
            ) {
                if (isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                } else if (meal != null) {
                    meal?.let { mealDetails ->
                        // Bild på måltiden

                        mealDetails.strMealThumb?.let { imageUrl ->
                            item {
                                Image(
                                    painter = rememberImagePainter(imageUrl),
                                    contentDescription = mealDetails.strMeal,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                )
                            }
                        }

                        //Favorite button
                        // Favorite button
                        item {
                            val isFavorite by viewModel.isMealFavorite(mealDetails.idMeal).collectAsState(initial = false)

                            Button(
                                onClick = {
                                    if (isFavorite) {
                                        viewModel.removeFromFavorites(mealDetails)
                                    } else {
                                        viewModel.addToFavorites(mealDetails)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isFavorite) "Remove from Favorites" else "Add to Favorites"
                                )
                            }
                        }


                        // Namnet på måltiden
                        item {
                            Text(
                                text = mealDetails.strMeal,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }

                        // Kategori
                        item {
                            Text(
                                text = "Category: ${mealDetails.strCategory ?: "Unknown"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Instruktioner
                        item {
                            Text(
                                text = "Instructions:",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = mealDetails.strInstructions ?: "No instructions available"
                            )
                        }

                        // Ingredienser och mått
                        item {
                            Text(
                                text = "Ingredients:",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        val ingredientsAndMeasures = listOf(
                            mealDetails.strIngredient1 to mealDetails.strMeasure1,
                            mealDetails.strIngredient2 to mealDetails.strMeasure2,
                            mealDetails.strIngredient3 to mealDetails.strMeasure3,
                            mealDetails.strIngredient4 to mealDetails.strMeasure4,
                            mealDetails.strIngredient5 to mealDetails.strMeasure5,
                            mealDetails.strIngredient6 to mealDetails.strMeasure6,
                            mealDetails.strIngredient7 to mealDetails.strMeasure7,
                            mealDetails.strIngredient8 to mealDetails.strMeasure8,
                            mealDetails.strIngredient9 to mealDetails.strMeasure9,
                            mealDetails.strIngredient10 to mealDetails.strMeasure10,
                            mealDetails.strIngredient11 to mealDetails.strMeasure11,
                            mealDetails.strIngredient12 to mealDetails.strMeasure12,
                            mealDetails.strIngredient13 to mealDetails.strMeasure13,
                            mealDetails.strIngredient14 to mealDetails.strMeasure14,
                            mealDetails.strIngredient15 to mealDetails.strMeasure15,
                            mealDetails.strIngredient16 to mealDetails.strMeasure16,
                            mealDetails.strIngredient17 to mealDetails.strMeasure17,
                            mealDetails.strIngredient18 to mealDetails.strMeasure18,
                            mealDetails.strIngredient19 to mealDetails.strMeasure19,
                            mealDetails.strIngredient20 to mealDetails.strMeasure20
                        )

                        items(ingredientsAndMeasures.filter { it.first.isNullOrBlank().not() }) { (ingredient, measure) ->
                            Text(
                                text = "$ingredient: ${measure ?: "No measurement available"}"
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = errorMessage ?: "No details found",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
                        )
                    }
                }
            }
        }
    )

}