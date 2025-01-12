package com.example.mobilaapplikationerlabc.Screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilaapplikationerlabc.ViewModels.NoFamilyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoFamilyScreen(navController: NavController, noFamilyViewModel: NoFamilyViewModel = viewModel()) {
    var familyName by remember { mutableStateOf("") }
    val context = LocalContext.current
    var familyId by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Centrera titeln
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Join or Create a Family",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

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
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(padding),
                verticalArrangement = Arrangement.Center
            ) {
                // Skapa en familj-sektionen
                Text(text = "Create a New Family", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = familyName,
                    onValueChange = { familyName = it },
                    label = { Text("Family Name") }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        noFamilyViewModel.createFamily(familyName) { success ->
                            if (success) {
                                Toast.makeText(context, "Family created!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Failed to create family", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(text = "Create Family")
                }

                Spacer(modifier = Modifier.height(60.dp))

                Text(text = "Join an Existing Family", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = familyId,
                    onValueChange = { familyId = it },
                    label = { Text("Family ID") }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        noFamilyViewModel.joinFamily(familyId) { success ->
                            if (success) {
                                Toast.makeText(context, "Successfully joined family!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Could not join family", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(text = "Join Family")
                }

            }

        }
    )
}
