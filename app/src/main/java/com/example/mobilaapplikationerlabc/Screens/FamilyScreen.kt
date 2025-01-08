package com.example.mobilaapplikationerlabc.Screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilaapplikationerlabc.DataClasses.Family
import com.example.mobilaapplikationerlabc.ViewModels.FamilyViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FamilyScreen(navController: NavController, familyViewModel: FamilyViewModel = viewModel()) {
    val family by familyViewModel.familyFlow.collectAsState()
    val leaveFamilySuccess by familyViewModel.leaveFamilySuccess.collectAsState()
    val documentId by familyViewModel.documentIdFlow.collectAsState()

    // Hämta familjen när skärmen öppnas
    LaunchedEffect(Unit) {
        familyViewModel.fetchCurrentUserFamily()

    }
    if (leaveFamilySuccess) {
        // När användaren har lämnat familjen, poppa tillbaka på stacken
        LaunchedEffect(leaveFamilySuccess) {
            navController.popBackStack() // Navigera tillbaka när användaren lämnar familjen
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Centrera både familjenamnet och dokument-ID i en Column
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally // Centrera texten horisontellt
                        ) {
                            Text(
                                text = family?.name ?: "Family",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = documentId ?: "Document ID",
                                style = MaterialTheme.typography.bodySmall, // Mindre text för dokument-ID
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Ljusare färg för sekundär text
                            )
                        }

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
            Column(
                modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Använd Scaffold-padding här
                .padding(16.dp), // Extra padding inuti
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                if (family == null || family?.members.isNullOrEmpty()) {
                    Text(text = "No members in your family.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(family!!.members) { memberUid ->
                            Spacer(modifier = Modifier.width(16.dp))
                            MemberRow(memberUid = memberUid)
                        }
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Sätter höjden för bottomBar
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(), // Täcker hela ytan av bottomBar
                    horizontalArrangement = Arrangement.Start // Justera knapparna så de ligger ihop utan mellanrum
                ) {
                    Button(
                        onClick = { /* TODO: Action for button 1 */ },
                        modifier = Modifier
                            .weight(1f) // Gör knappen fyrkantig genom att fylla höjden och bredden
                            .fillMaxHeight()
                            .padding(0.dp), // Ingen padding mellan knapparna
                        shape = RectangleShape // Fyrkantig form
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star", // Beskrivning för tillgänglighet
                            modifier = Modifier.size(45.dp),  // Sätt storlek på själva ikonen
                            tint = Color.Yellow
                        )
                    }

                    Button(
                        onClick = {
                            navController.navigate("familyCart")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(0.dp),
                        shape = RectangleShape // Fyrkantig form
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Shopping Cart", // Beskrivning för tillgänglighet
                            modifier = Modifier.size(45.dp) // Justera storleken på ikonen
                        )
                    }

                    Button(
                        onClick = { familyViewModel.leaveFamily() },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(0.dp),
                        shape = RectangleShape // Fyrkantig form
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Exit", // Beskrivning av ikonen för tillgänglighet
                            modifier = Modifier.size(45.dp),  // Sätt storlek på själva ikonen
                            tint = Color.Red
                        )
                    }

                }
            }
        }

    )
}

@Composable
fun MemberRow(memberUid: String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Skapa MutableState för att lagra användarens namn och e-post
    var memberName by remember { mutableStateOf("Loading...") }
    var memberEmail by remember { mutableStateOf("Loading...") }

    // Hämta användarens information
    LaunchedEffect(memberUid) {
        db.collection("users").document(memberUid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    memberName = document.getString("name") ?: "No Name"
                    memberEmail = document.getString("email") ?: "No Email"
                } else {
                    memberName = "No Name"
                    memberEmail = "No Email"
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching user data", e)
                memberName = "Error"
                memberEmail = "Error"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = memberName,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp) // Öka textstorleken här
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = memberEmail,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

