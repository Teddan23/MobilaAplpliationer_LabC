package com.example.mobilaapplikationerlabc.Models

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FamilyCartModel {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun fetchShoppingList(): List<String> {
        val currentUserUid = auth.currentUser?.uid ?: return emptyList()

        return try {
            val documents = db.collection("families")
                .whereArrayContains("members", currentUserUid)
                .limit(1)
                .get()
                .await()

            if (documents.isEmpty) {
                Log.e("FamilyCartModel", "No family found for current user.")
                emptyList()
            } else {
                val familyDoc = documents.documents[0]
                familyDoc.get("shoppingList") as? List<String> ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("FamilyCartModel", "Error fetching shopping list", e)
            emptyList()
        }
    }

    suspend fun addItem(newItem: String): List<String> {
        val currentUserUid = auth.currentUser?.uid ?: return emptyList()

        return try {
            val documents = db.collection("families")
                .whereArrayContains("members", currentUserUid)
                .limit(1)
                .get()
                .await()

            if (documents.isEmpty) {
                Log.e("FamilyCartModel", "No family found for current user.")
                emptyList()
            } else {
                val familyDoc = documents.documents[0]
                val familyId = familyDoc.id
                val currentList = familyDoc.get("shoppingList") as? MutableList<String> ?: mutableListOf()

                currentList.add(newItem)
                db.collection("families")
                    .document(familyId)
                    .update("shoppingList", currentList)
                    .await()

                currentList
            }
        } catch (e: Exception) {
            Log.e("FamilyCartModel", "Error adding item", e)
            emptyList()
        }
    }

    suspend fun removeItem(item: String): List<String> {
        val currentUserUid = auth.currentUser?.uid ?: return emptyList()

        return try {
            val documents = db.collection("families")
                .whereArrayContains("members", currentUserUid)
                .limit(1)
                .get()
                .await()

            if (documents.isEmpty) {
                Log.e("FamilyCartModel", "No family found for current user.")
                emptyList()
            } else {
                val familyDoc = documents.documents[0]
                val familyId = familyDoc.id
                val currentList = familyDoc.get("shoppingList") as? MutableList<String> ?: mutableListOf()

                if (currentList.contains(item)) {
                    currentList.remove(item)
                    db.collection("families")
                        .document(familyId)
                        .update("shoppingList", currentList)
                        .await()
                }

                currentList
            }
        } catch (e: Exception) {
            Log.e("FamilyCartModel", "Error removing item", e)
            emptyList()
        }
    }
}
