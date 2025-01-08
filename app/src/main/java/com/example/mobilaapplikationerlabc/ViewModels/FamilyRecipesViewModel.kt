package com.example.mobilaapplikationerlabc.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.mobilaapplikationerlabc.DataClasses.SimpleMeal
import com.example.mobilaapplikationerlabc.model.Meal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FamilyRecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val _recipesFlow = MutableStateFlow<List<SimpleMeal>>(emptyList())
    val recipesFlow: StateFlow<List<SimpleMeal>> = _recipesFlow.asStateFlow()
    private val auth = FirebaseAuth.getInstance() // Definieras här som instansvariabel

    fun fetchFamilyRecipes() {
        val userId = auth.currentUser?.uid ?: return // Hämta nuvarande användarens ID
        // Hämta alla familjer där användaren är medlem
        db.collection("families")
            .whereArrayContains("members", userId)  // Filtrera på familjer där "members" innehåller användarens UID
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Hämta den första familjen som matchar användarens UID
                    val familyDocument = querySnapshot.documents[0]
                    val recipeIds = familyDocument["recipes"] as? List<String> ?: emptyList()

                    if (recipeIds.isNotEmpty()) {
                        fetchRecipesByIds(recipeIds)
                    } else {
                        _recipesFlow.value = emptyList() // Ingen recept för familjen
                    }
                } else {
                    Log.e("FamilyRecipesViewModel", "User is not part of any family")
                    _recipesFlow.value = emptyList() // Om användaren inte är med i någon familj
                }
            }
            .addOnFailureListener { e ->
                Log.e("FamilyRecipesViewModel", "Error fetching family document", e)
            }
    }

    private fun fetchRecipesByIds(recipeIds: List<String>) {
        db.collection("recipes")
            .whereIn(FieldPath.documentId(), recipeIds) // Hämta dokument med de specifika ID:n
            .get()
            .addOnSuccessListener { querySnapshot ->
                val meals = querySnapshot.documents
                    .mapNotNull { document ->
                        val strCategory = document["strCategory"] as? String
                        val strMeal = document["strMeal"] as? String
                        val idMeal = document.id
                        if (strCategory != null && strMeal != null) {
                            SimpleMeal(idMeal, strCategory, strMeal) // Skapa en enklare representation av Meal
                        } else {
                            null // Om nödvändiga fält saknas, ignorera dokumentet
                        }
                    }
                _recipesFlow.value = meals
            }
            .addOnFailureListener { e ->
                Log.e("FamilyRecipesViewModel", "Error fetching recipes by IDs", e)
            }
    }

    // Konverteringsfunktion från SimpleMeal till Mea


}
