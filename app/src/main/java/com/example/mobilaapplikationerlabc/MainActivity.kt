package com.example.mobilaapplikationerlabc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilaapplikationerlabc.Navigation.NavigationHost
import com.example.mobilaapplikationerlabc.Screens.HomePageScreen
import com.example.mobilaapplikationerlabc.Screens.LoginScreen
import com.example.mobilaapplikationerlabc.ViewModelFactories.LoginViewModelFactory
import com.example.mobilaapplikationerlabc.ViewModels.LoginViewModel
import com.example.mobilaapplikationerlabc.ui.theme.MobilaApplikationerLabCTheme

class MainActivity : ComponentActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Skapa ViewModel via factory
        val factory = LoginViewModelFactory(application)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        setContent {
            MobilaApplikationerLabCTheme {
                NavigationHost(startDestination = if (loginViewModel.isUserLoggedIn) "home" else "login", loginViewModel)
            }
        }
    }
}
