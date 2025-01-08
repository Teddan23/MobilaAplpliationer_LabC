package com.example.mobilaapplikationerlabc.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilaapplikationerlabc.ViewModels.FamilyRecipesViewModel
import com.example.mobilaapplikationerlabc.ViewModels.FamilyViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.mobilaapplikationerlabc.DataClasses.SimpleMeal

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FamilyRecipesScreen(
    navController: NavController,
    familyRecipesViewModel: FamilyRecipesViewModel = viewModel()
) {
    val isLoading by familyRecipesViewModel.isLoading.collectAsState()
    val recipes by familyRecipesViewModel.recipesFlow.collectAsState()

    LaunchedEffect(Unit) {
        familyRecipesViewModel.fetchFamilyRecipes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Family Recipes",
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
        content = { paddingValues ->
            if(isLoading){
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else if (recipes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No recipes found.")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp).padding(top = paddingValues.calculateTopPadding())) {
                    items(recipes) { recipe ->
                        SimpleMealItem(meal = recipe, navController = navController)
                    }
                }

            }
        }
    )
}

@Composable
fun SimpleMealItem(meal: SimpleMeal, navController: NavController) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                navController.navigate("mealDetail/${meal.idMeal}")
            }
    ) {
        Text(
            text = meal.strMeal,
            style = TextStyle(fontSize = 23.sp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = meal.strCategory,
            style = TextStyle(fontSize = 14.sp)
            )
        Spacer(modifier = Modifier.height(4.dp))
    }
}


