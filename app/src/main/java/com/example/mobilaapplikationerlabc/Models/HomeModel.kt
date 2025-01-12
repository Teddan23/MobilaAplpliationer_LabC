package com.example.mobilaapplikationerlabc.Models

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeModel {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "HomeModel"
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
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("families")
                .whereArrayContains("members", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val userFoundInFamily = !documents.isEmpty
                    onComplete(userFoundInFamily)
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        } else {
            onComplete(false)
        }
    }

    fun addUserToDbIfNeeded() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val userRef = db.collection("users").document(user.uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d(TAG, "User already exists in the database")
                } else {
                    val userMap = hashMapOf(
                        "name" to user.displayName,
                        "email" to user.email
                    )

                    userRef.set(userMap)
                        .addOnSuccessListener {
                            Log.d(TAG, "User information saved successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error saving user information", e)
                        }
                }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error checking user existence", e)
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        Log.d(TAG, "User logged out")
    }
}
