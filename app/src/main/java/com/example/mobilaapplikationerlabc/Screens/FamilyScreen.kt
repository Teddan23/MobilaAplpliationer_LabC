package com.example.mobilaapplikationerlabc.Screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
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

    val isLoading by familyViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        familyViewModel.fetchCurrentUserFamily()

    }
    if (leaveFamilySuccess) {
        LaunchedEffect(leaveFamilySuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = family?.name ?: "Family",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = ("Id: " + (documentId ?: "Document ID")),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                .padding(paddingValues)
                .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                if(isLoading){
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
                }
                else{
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
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        onClick = { navController.navigate("familyRecipes") },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(0.dp),
                        shape = RectangleShape
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            modifier = Modifier.size(45.dp),
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
                        shape = RectangleShape
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Shopping Cart",
                            modifier = Modifier.size(45.dp)
                        )
                    }

                    Button(
                        onClick = { familyViewModel.leaveFamily() },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(0.dp),
                        shape = RectangleShape
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Exit",
                            modifier = Modifier.size(45.dp),
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
    //val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var memberName by remember { mutableStateOf("Loading...") }
    var memberEmail by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }

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
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching user data", e)
                memberName = "Error"
                memberEmail = "Error"
                isLoading = false
            }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(isLoading){

        }
        else{
            Icon(
                imageVector = Icons.Filled.Face,
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 16.dp),
                tint = Color.Black
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = memberName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = memberEmail,
                    style = MaterialTheme.typography.bodyMedium
                )

            }
        }
    }


}

