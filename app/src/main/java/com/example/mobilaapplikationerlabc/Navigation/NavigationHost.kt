package com.example.mobilaapplikationerlabc.Navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilaapplikationerlabc.Screens.ExploreScreen
import com.example.mobilaapplikationerlabc.Screens.FamilyCartScreen
import com.example.mobilaapplikationerlabc.Screens.FamilyRecipesScreen
import com.example.mobilaapplikationerlabc.Screens.FamilyScreen
import com.example.mobilaapplikationerlabc.Screens.NoFamilyScreen
import com.example.mobilaapplikationerlabc.Screens.HomePageScreen
import com.example.mobilaapplikationerlabc.Screens.LoginScreen
import com.example.mobilaapplikationerlabc.Screens.MealDetailScreen
import com.example.mobilaapplikationerlabc.Screens.RandomScreen
import com.example.mobilaapplikationerlabc.ViewModels.LoginViewModel

@Composable
fun NavigationHost(
    startDestination: String,
    loginViewModel: LoginViewModel
) {
    val navController = rememberNavController()
    Log.d("Navigation", "NavController created")
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            Log.d("Navigation", "Navigating to login screen")
            LoginScreen(
                navController = navController,
                loginViewModel = loginViewModel
            )
        }
        composable("home") {
            Log.d("Navigation", "Navigating to home screen")
            HomePageScreen(navController = navController)
        }
        composable("family") {
            FamilyScreen(navController = navController)
        }
        composable("noFamily") {
            NoFamilyScreen(navController = navController)
        }
        composable("explore") {
            ExploreScreen(navController = navController)
        }
        composable("mealDetail/{mealId}") { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
            MealDetailScreen(mealId = mealId, navController = navController, viewModel = viewModel())
        }
        composable("random"){
            RandomScreen(navController = navController)
        }
        composable("familyCart") {
            FamilyCartScreen(navController = navController)
        }
        composable("familyRecipes") {
            FamilyRecipesScreen(navController = navController)
        }
    }
}