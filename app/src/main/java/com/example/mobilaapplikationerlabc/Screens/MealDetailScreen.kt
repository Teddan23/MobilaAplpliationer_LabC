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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(mealId: String, navController: NavController,  viewModel: MealViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    var meal by remember { mutableStateOf<Meal?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isFavorite by remember { mutableStateOf(false) }

    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(TheMealDBService::class.java)

    LaunchedEffect(mealId) {
        service.getMealDetails(mealId).enqueue(object : Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                if (response.isSuccessful) {
                    meal = response.body()?.meals?.firstOrNull()
                    meal?.let {
                        viewModel.checkIfMealIsInFamily(it.idMeal) { isInFamily ->
                            isFavorite = isInFamily
                        }
                    }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Meal Details",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
,
        content = { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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

                        item {
                            Button(
                                onClick = {
                                    if (isFavorite) {
                                        viewModel.removeFromFavorites(mealDetails)
                                    } else {
                                        viewModel.saveMeal(mealDetails)
                                    }
                                    isFavorite = !isFavorite
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isFavorite) MaterialTheme.colorScheme.error else Color.DarkGray
                                )
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Clear else Icons.Filled.Star,
                                    contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                                    modifier = Modifier.size(45.dp),
                                    tint = if (isFavorite) Color.White else Color.Yellow
                                )
                            }


                            /*Button(
                                onClick = {
                                    if (isFavorite) {
                                        // Om måltiden är i favoriter, ta bort den från familjens lista
                                        viewModel.removeFromFavorites(mealDetails)
                                    } else {
                                        // Om måltiden inte är i favoriter, lägg till den i familjens lista
                                        viewModel.saveMeal(mealDetails)
                                    }
                                    // Uppdatera om måltiden finns i familjens lista efter knapptryck
                                    /*viewModel.checkIfMealIsInFamily(mealDetails.idMeal) { isInFamily ->
                                        isFavorite = isInFamily
                                    }*/
                                    isFavorite = !isFavorite
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isFavorite) "Remove from Favorites" else "Add to Favorites"
                                )
                            }*/
                        }

                        item {
                            Text(
                                text = mealDetails.strMeal,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }

                        item {
                            Text(
                                text = "Category: ${mealDetails.strCategory ?: "Unknown"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        item {
                            Text(
                                text = "Instructions:",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = mealDetails.strInstructions ?: "No instructions available"
                            )
                        }

                        item {
                            Text(
                                text = "Ingredients:",
                                style = MaterialTheme.typography.titleLarge
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