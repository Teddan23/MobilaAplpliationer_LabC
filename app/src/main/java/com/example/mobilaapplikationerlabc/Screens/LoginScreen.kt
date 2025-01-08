package com.example.mobilaapplikationerlabc.Screens

import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilaapplikationerlabc.ViewModels.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import androidx.compose.material3.Icon
import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import com.example.mobilaapplikationerlabc.R

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    navController: NavController
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        loginViewModel.handleSignInResult(task) { success, displayName ->
            if (success) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                Log.e("LoginScreen", "Login failed: $displayName")
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Logga in med",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { launcher.launch(loginViewModel.signInWithGoogleIntent()) },
                    modifier = Modifier.padding(0.dp)
                ) {
                    // Visa Google-ikonen
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google SignIn"
                    )
                }
            }
        }
    }
}
