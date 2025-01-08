package com.example.mobilaapplikationerlabc.ViewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.mobilaapplikationerlabc.DataClasses.Family
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NoFamilyViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "FamilyViewModelTag"
    }

    fun createFamily(
        name: String,
        onComplete: (Boolean) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(getApplication(), "User not logged in", Toast.LENGTH_SHORT).show()
            onComplete(false)
            return
        }

        // Skapa Family-objektet med aktuell användare som första medlem
        val family = Family(
            name = name,
            members = listOf(currentUser.uid), // Användarens UID läggs till som första medlem
            recipes = listOf(),           // Tom lista med recept
            shoppingList = listOf()       // Tom inköpslista
        )

        // Lägger till familjen i Firestore (ID genereras automatiskt)
        db.collection("families")
            .add(family)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }


    fun joinFamily(
        familyId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            db.collection("families").document(familyId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val existingFamily = document.toObject(Family::class.java)
                        if (existingFamily != null) {
                            // Uppdatera listan av medlemmar genom att lägga till användarens UID
                            val updatedMembers = existingFamily.members.toMutableList()
                            updatedMembers.add(currentUser.uid)  // Lägg till användarens UID till listan

                            // Uppdatera familjedokumentet i Firestore
                            db.collection("families").document(familyId)
                                .update("members", updatedMembers)
                                .addOnSuccessListener {
                                    onComplete(true)
                                }
                                .addOnFailureListener {
                                    onComplete(false)
                                }
                        } else {
                            onComplete(false)
                        }
                    } else {
                        onComplete(false)
                    }
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        } else {
            onComplete(false)
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


}