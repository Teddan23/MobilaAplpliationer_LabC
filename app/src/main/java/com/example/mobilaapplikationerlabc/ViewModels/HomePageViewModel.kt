package com.example.mobilaapplikationerlabc.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class HomePageViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "HomePageViewModelTag"
    }

    init {
        // Kör metoden för att lägga till användaren i databasen om det behövs när ViewModel skapas
        addUserToDbIfNeeded()
    }

    val isUserLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    fun getUserName(): String {
        return if (isUserLoggedIn) {
            firebaseAuth.currentUser?.displayName ?: "No Name"
        } else {
            "USER NOT LOGGED IN!"
        }
    }

    fun getEmail(): String {
        return if (isUserLoggedIn) {
            firebaseAuth.currentUser?.email ?: "No Email"
        } else {
            "USER NOT LOGGED IN!"
        }
    }

    fun checkIfInFamily(onComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Hämta UID för den inloggade användaren
            val userId = currentUser.uid

            // Kontrollera om det finns någon familj där användaren finns i medlemmarna
            db.collection("families")
                .whereArrayContains("members", userId)  // Filtrera på familjer där "members" innehåller användarens UID
                .get()
                .addOnSuccessListener { documents ->
                    // Om vi hittar dokument, betyder det att användaren är medlem i åtminstone en familj
                    val userFoundInFamily = !documents.isEmpty  // Vi använder isEmpty() istället för isNotEmpty
                    onComplete(userFoundInFamily)
                }
                .addOnFailureListener {
                    // Om ett fel inträffar, returnera false
                    onComplete(false)
                }
        } else {
            onComplete(false) // Om användaren inte är inloggad, returnera false
        }
    }

    fun addUserToDbIfNeeded() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        if (user != null) {
            val userRef = db.collection("users").document(user.uid)

            // Kolla om användaren redan finns i databasen
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Användaren finns redan i databasen
                    Log.d("Firestore", "User already exists in the database")
                } else {
                    // Användaren finns inte, så vi lägger till dem
                    val userMap = hashMapOf(
                        "name" to user.displayName,
                        "email" to user.email
                    )

                    userRef.set(userMap)
                        .addOnSuccessListener {
                            Log.d("Firestore", "User information saved successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error saving user information", e)
                        }
                }
            }.addOnFailureListener { e ->
                Log.w("Firestore", "Error checking user existence", e)
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        Log.d(TAG, "User logged out")
    }
}
