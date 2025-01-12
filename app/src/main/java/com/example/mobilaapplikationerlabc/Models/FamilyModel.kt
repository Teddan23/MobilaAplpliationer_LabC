package com.example.mobilaapplikationerlabc.Models

import android.util.Log
import com.example.mobilaapplikationerlabc.DataClasses.Family
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FamilyModel {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "FamilyModel"
    }

    suspend fun fetchCurrentUserFamily(): Pair<Family?, String?> {
        val currentUserId = auth.currentUser?.uid ?: return Pair(null, null)

        return try {
            val querySnapshot = db.collection("families")
                .whereArrayContains("members", currentUserId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val familyDocument = querySnapshot.documents[0]
                val family = familyDocument.toObject(Family::class.java)
                Pair(family, familyDocument.id)
            } else {
                Pair(null, null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching current user family", e)
            Pair(null, null)
        }
    }

    suspend fun leaveFamily(): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false
        val querySnapshot = db.collection("families")
            .whereArrayContains("members", currentUserId)
            .get()
            .await()

        return if (!querySnapshot.isEmpty) {
            val familyDocument = querySnapshot.documents[0]
            val familyId = familyDocument.id
            val family = familyDocument.toObject(Family::class.java)

            if (family != null) {
                val updatedMembers = family.members.toMutableList()
                if (updatedMembers.size == 1) {
                    db.collection("families").document(familyId).delete().await()
                } else {
                    updatedMembers.remove(currentUserId)
                    db.collection("families").document(familyId).update("members", updatedMembers)
                        .await()
                }
                true
            } else {
                false
            }
        } else {
            false
        }
    }
}
