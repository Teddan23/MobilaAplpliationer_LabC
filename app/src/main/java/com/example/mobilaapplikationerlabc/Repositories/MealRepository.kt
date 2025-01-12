package com.example.mobilaapplikationerlabc.Repositories

import android.util.Log
import com.example.mobilaapplikationerlabc.model.Meal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MealRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun isMealDuplicate(mealId: String, onResult: (Boolean) -> Unit) {
        db.collection("recipes")
            .document(mealId)
            .get()
            .addOnSuccessListener { document ->
                onResult(document.exists())
            }
            .addOnFailureListener { e ->
                Log.e("RecipeRepository", "Error checking if meal is duplicate", e)
                onResult(false)
            }
    }

    fun saveMeal(meal: Meal) {
        checkIfInFamily { isInFamily ->
            if (!isInFamily) {
                Log.d("RecipeRepository", "User is not in any family, skipping save.")
                return@checkIfInFamily
            }

            isMealDuplicate(meal.idMeal) { isDuplicate ->
                if (isDuplicate) {
                    Log.d("RecipeRepository", "Meal already exists, skipping save.")

                } else {
                    val mealRef = db.collection("recipes").document(meal.idMeal)
                    mealRef.set(meal)
                        .addOnSuccessListener {
                            Log.d("RecipeRepository", "Meal saved successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("RecipeRepository", "Error saving meal", e)
                        }
                }

                addMealToFamily(meal.idMeal)
            }
        }
    }

    fun checkIfInFamily(onComplete: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

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

    private fun addMealToFamily(mealId: String) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("families")
                .whereArrayContains("members", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val familyDocument = documents.first()
                        val familyId = familyDocument.id
                        val recipesList = familyDocument.get("recipes") as? List<String> ?: emptyList()

                        if (!recipesList.contains(mealId)) {
                            val updatedRecipes = recipesList.toMutableList().apply {
                                add(mealId)
                            }

                            db.collection("families")
                                .document(familyId)
                                .update("recipes", updatedRecipes)
                                .addOnSuccessListener {
                                    Log.d("RecipeRepository", "Meal ID added to family's recipes list successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("RecipeRepository", "Error adding meal ID to family's recipes list", e)
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("RecipeRepository", "Error fetching family document", e)
                }
        }
    }

    fun removeMealFromFamily(mealId: String) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("families")
                .whereArrayContains("members", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val familyDocument = documents.first()
                        val familyId = familyDocument.id
                        val recipesList = familyDocument.get("recipes") as? List<String> ?: emptyList()

                        if (recipesList.contains(mealId)) {
                            val updatedRecipes = recipesList.toMutableList().apply {
                                remove(mealId)
                            }

                            db.collection("families")
                                .document(familyId)
                                .update("recipes", updatedRecipes)
                                .addOnSuccessListener {
                                    Log.d("RecipeRepository", "Meal ID removed from family's recipes list successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("RecipeRepository", "Error removing meal ID from family's recipes list", e)
                                }
                        } else {
                            Log.d("RecipeRepository", "Meal ID not found in family's recipes list")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("RecipeRepository", "Error fetching family document", e)
                }
        }
    }


    fun isMealInFamily(mealId: String, onResult: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("families")
                .whereArrayContains("members", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val familyDocument = documents.first()
                        val recipesList = familyDocument.get("recipes") as? List<String> ?: emptyList()

                        val isMealInFamily = recipesList.contains(mealId)
                        onResult(isMealInFamily)
                    } else {
                        onResult(false)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("RecipeRepository", "Error fetching family document", e)
                    onResult(false)
                }
        } else {
            onResult(false)
        }
    }

}
