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
import androidx.compose.material.icons.filled.Home
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
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = {
                    navController.navigate("explore")
                },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Explore",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = {
                    navController.navigate("random")
                },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Random",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = {

                    Log.d("HomePageScreenTag", "Button clicked. ")

                    homePageViewModel.checkIfInFamily { isInFamily ->
                        if (isInFamily) {
                            navController.navigate("family")
                        } else {
                            navController.navigate("noFamily")
                        }
                    }
                },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Family",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = {
                    homePageViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Logout",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Black
                )
            }
        }
    }
}
