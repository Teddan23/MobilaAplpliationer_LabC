package com.example.mobilaapplikationerlabc.ViewModels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    lateinit var googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("969162192870-9mdfet2vdtas9907la28sskmv54hmcea.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(application, gso)
    }

    val isUserLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    fun signInWithGoogleIntent(): Intent = googleSignInClient.signInIntent

    fun handleSignInResult(task: Task<GoogleSignInAccount>, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val account = task.getResult(Exception::class.java)
                account?.idToken?.let { idToken ->
                    firebaseAuthWithGoogle(idToken, onResult)
                } ?: onResult(false, "No ID token found")
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, firebaseAuth.currentUser?.displayName)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}
