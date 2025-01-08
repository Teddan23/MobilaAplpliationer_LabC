package com.example.mobilaapplikationerlabc.Screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilaapplikationerlabc.ViewModels.HomePageViewModel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun HomePageScreen(
    navController: NavController,
    homePageViewModel: HomePageViewModel = viewModel()
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome, ${homePageViewModel.getUserName()}")
            //Text(text = "Email: ${homePageViewModel.getEmail()}")
            Spacer(modifier = Modifier.height(16.dp))

            // För Explore-knappen (Search ikon)
            IconButton(
                onClick = {
                    // Åtgärder för Explore-knappen
                    navController.navigate("explore")
                },
                modifier = Modifier.size(80.dp)  // Sätt storlek på hela knappen
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Explore",
                    modifier = Modifier.size(50.dp),  // Sätt storlek på själva ikonen
                    tint = Color.Black  // Sätt en färg på ikonen (valfritt)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))  // Lägg till avstånd mellan knapparna

            //Spacer(modifier = Modifier.height(16.dp))  // Lägg till avstånd mellan knapparna

            // För Surprise Me-knappen (Random)
            IconButton(
                onClick = {
                    navController.navigate("random")
                },
                modifier = Modifier.size(80.dp)  // Sätt storlek på hela knappen
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Random",
                    modifier = Modifier.size(50.dp),  // Sätt storlek på själva ikonen
                    tint = Color.Red  // Sätt en färg på ikonen (valfritt)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            // För Family-knappen (Face ikon)

            // För Family-knappen (Face ikon)
            IconButton(
                onClick = {
                    // Åtgärder för Family-knappen
                    //navController.navigate("family")

                    //navController.navigate("noFamily")

                    Log.d("HomePageScreenTag", "Button clicked. ")

                    homePageViewModel.checkIfInFamily { isInFamily ->
                        if (isInFamily) {
                            // Om användaren är med i en familj, navigera till FamilyScreen
                            navController.navigate("family")
                        } else {
                            // Om användaren inte är med i någon familj, navigera till NoFamilyScreen
                            navController.navigate("noFamily")
                        }
                    }
                },
                modifier = Modifier.size(80.dp)  // Sätt storlek på hela knappen
            ) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Family",
                    modifier = Modifier.size(50.dp),  // Sätt storlek på själva ikonen
                    tint = Color.Black  // Sätt en färg på ikonen (valfritt)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))  // Lägg till avstånd mellan knapparna

            // För Logout-knappen (ExitToApp ikon)
            IconButton(
                onClick = {
                    homePageViewModel.signOut()  // Logga ut användaren
                    navController.navigate("login") {  // Navigera till login
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.size(80.dp)  // Sätt storlek på hela knappen
            ) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Logout",
                    modifier = Modifier.size(50.dp),  // Sätt storlek på själva ikonen
                    tint = Color.Black  // Sätt en färg på ikonen (valfritt)
                )
            }
        }
    }
}
