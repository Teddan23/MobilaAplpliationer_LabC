package com.example.mobilaapplikationerlabc.Repositories

import android.util.Log
import com.example.mobilaapplikationerlabc.DataClasses.Family
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NoFamilyRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun createFamily(name: String): Boolean {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("NoFamilyRepository", "User not logged in")
            return false
        }

        val family = Family(
            name = name,
            members = listOf(currentUser.uid),
            recipes = listOf(),
            shoppingList = listOf()
        )

        return try {
            db.collection("families").add(family).await()
            true
        } catch (e: Exception) {
            Log.e("NoFamilyRepository", "Error creating family", e)
            false
        }
    }

    suspend fun joinFamily(familyId: String): Boolean {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("NoFamilyRepository", "User not logged in")
            return false
        }

        return try {
            val familyDoc = db.collection("families").document(familyId).get().await()
            if (familyDoc.exists()) {
                val existingFamily = familyDoc.toObject(Family::class.java)
                existingFamily?.let {
                    val updatedMembers = it.members.toMutableList()
                    updatedMembers.add(currentUser.uid)

                    db.collection("families").document(familyId)
                        .update("members", updatedMembers).await()
                    true
                } ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("NoFamilyRepository", "Error joining family", e)
            false
        }
    }
}
