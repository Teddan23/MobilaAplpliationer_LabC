package com.example.mobilaapplikationerlabc.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilaapplikationerlabc.ViewModels.FamilyCartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FamilyCartScreen(
    navController: NavController,
    familyCartViewModel: FamilyCartViewModel = viewModel()
) {
    val shoppingList = familyCartViewModel.shoppingListFlow.collectAsState().value

    var showDialog by remember { mutableStateOf(false) }  // Styr om dialogen ska visas
    var newItem by remember { mutableStateOf(TextFieldValue("")) }

    // Kör metoden för att hämta data när skärmen öppnas
    LaunchedEffect(Unit) {
        familyCartViewModel.fetchShoppingList()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Centrera texten i Box
                        Text(
                            text = "Shopping List",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        // Placera navigation-ikonen till vänster
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            if (shoppingList.isEmpty()) {
                // Visa ett meddelande om listan är tom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No items in the shopping list.")
                }
            } else {
                // Visa shoppinglistan
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp) // Extra padding inuti
                ) {
                    items(shoppingList) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.weight(1f) // Fyller upp tillgängligt utrymme
                            )

                            Button(
                                onClick = { familyCartViewModel.removeItem(item) },
                                modifier = Modifier.padding(start = 8.dp),
                                shape = RoundedCornerShape(4.dp), // Lätt avrundade hörn med radie 4dp
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Remove Item"
                                )
                            }

                        }
                    }
                }
            }
        },
        bottomBar = {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth() // Täcker hela bredden
                    .height(80.dp), // Sätter höjden för bottomBar-knappen
                shape = RectangleShape, // Fyrkantig form utan rundade hörn
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add item to cart",
                    modifier = Modifier.size(45.dp)
                )
            }
        }

    )
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Add New Item") },
            text = {
                TextField(
                    value = newItem,
                    onValueChange = { newItem = it },
                    placeholder = { Text(text = "Enter item name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newItem.text.isNotBlank()) {
                            familyCartViewModel.addItem(newItem.text)  // Lägg till varan
                            newItem = TextFieldValue("")  // Rensa textfältet
                            showDialog = false  // Stäng dialogen
                        }
                    }
                ) {
                    Text(text = "Add")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
