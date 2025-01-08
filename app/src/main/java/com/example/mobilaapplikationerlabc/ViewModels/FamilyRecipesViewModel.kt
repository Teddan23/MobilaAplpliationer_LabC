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
    private val auth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchFamilyRecipes() {
        _isLoading.value = true
        val userId = auth.currentUser?.uid ?: return
        db.collection("families")
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val familyDocument = querySnapshot.documents[0]
                    val recipeIds = familyDocument["recipes"] as? List<String> ?: emptyList()

                    if (recipeIds.isNotEmpty()) {
                        fetchRecipesByIds(recipeIds)
                    } else {
                        _recipesFlow.value = emptyList()
                        _isLoading.value = false
                    }
                } else {
                    Log.e("FamilyRecipesViewModel", "User is not part of any family")
                    _recipesFlow.value = emptyList()
                    _isLoading.value = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("FamilyRecipesViewModel", "Error fetching family document", e)
                _isLoading.value = false
            }
    }

    private fun fetchRecipesByIds(recipeIds: List<String>) {
        db.collection("recipes")
            .whereIn(FieldPath.documentId(), recipeIds)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val meals = querySnapshot.documents
                    .mapNotNull { document ->
                        val strCategory = document["strCategory"] as? String
                        val strMeal = document["strMeal"] as? String
                        val idMeal = document.id
                        if (strCategory != null && strMeal != null) {
                            SimpleMeal(idMeal, strCategory, strMeal)
                        } else {
                            null
                        }
                    }
                _recipesFlow.value = meals
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                Log.e("FamilyRecipesViewModel", "Error fetching recipes by IDs", e)
                _isLoading.value = false
            }
    }

}
