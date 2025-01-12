package com.example.mobilaapplikationerlabc.Repositories

import android.util.Log
import com.example.mobilaapplikationerlabc.DataClasses.SimpleMeal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FamilyRecipesRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getFamilyRecipes(): List<SimpleMeal> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        return try {
            val familyQuerySnapshot = db.collection("families")
                .whereArrayContains("members", userId)
                .get()
                .await()

            if (familyQuerySnapshot.isEmpty) {
                Log.e("FamilyRecipesRepository", "User is not part of any family")
                return emptyList()
            }

            val familyDocument = familyQuerySnapshot.documents[0]
            val recipeIds = familyDocument["recipes"] as? List<String> ?: return emptyList()

            fetchRecipesByIds(recipeIds)
        } catch (e: Exception) {
            Log.e("FamilyRecipesRepository", "Error fetching family recipes", e)
            emptyList()
        }
    }

    private suspend fun fetchRecipesByIds(recipeIds: List<String>): List<SimpleMeal> {
        return try {
            val recipesQuerySnapshot = db.collection("recipes")
                .whereIn(FieldPath.documentId(), recipeIds)
                .get()
                .await()

            recipesQuerySnapshot.documents.mapNotNull { document ->
                val strCategory = document["strCategory"] as? String
                val strMeal = document["strMeal"] as? String
                val idMeal = document.id

                if (strCategory != null && strMeal != null) {
                    SimpleMeal(idMeal, strCategory, strMeal)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FamilyRecipesRepository", "Error fetching recipes by IDs", e)
            emptyList()
        }
    }
}
