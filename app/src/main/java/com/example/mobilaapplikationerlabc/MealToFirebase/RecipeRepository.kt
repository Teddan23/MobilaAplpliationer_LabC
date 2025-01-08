package com.example.mobilaapplikationerlabc.MealToFirebase

import android.util.Log
import com.example.mobilaapplikationerlabc.model.Meal
import com.example.mobilaapplikationerlabc.model.MealResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RecipeRepository {

    private val db = FirebaseFirestore.getInstance()

    // Kollar om en måltid med samma idMeal redan finns i Firestore
    private fun isMealDuplicate(mealId: String, onResult: (Boolean) -> Unit) {
        db.collection("recipes")
            .document(mealId)
            .get()
            .addOnSuccessListener { document ->
                // Om dokumentet existerar, är det en duplicerad måltid
                onResult(document.exists())
            }
            .addOnFailureListener { e ->
                Log.e("RecipeRepository", "Error checking if meal is duplicate", e)
                onResult(false) // Om vi misslyckas att hämta, antar vi att den inte finns
            }
    }

    // Spara en måltid om den inte redan finns
    fun saveMeal(meal: Meal) {
        // Kontrollera först om användaren är medlem i en familj
        checkIfInFamily { isInFamily ->
            if (!isInFamily) {
                Log.d("RecipeRepository", "User is not in any family, skipping save.")
                return@checkIfInFamily // Avbryt om användaren inte tillhör någon familj
            }

            // Om användaren är medlem i en familj, kolla om måltiden redan finns
            isMealDuplicate(meal.idMeal) { isDuplicate ->
                if (isDuplicate) {
                    Log.d("RecipeRepository", "Meal already exists, skipping save.")

                } else {
                    // Om den inte är en duplikat, spara den
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

    private fun addMealToFamily(mealId: String) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Hämta UID för den inloggade användaren
            val userId = currentUser.uid

            // Hitta familjen där användaren är medlem
            db.collection("families")
                .whereArrayContains("members", userId) // Filtrera på familjer där "members" innehåller användarens UID
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val familyDocument = documents.first()
                        val familyId = familyDocument.id
                        val recipesList = familyDocument.get("recipes") as? List<String> ?: emptyList()

                        // Om receptet inte redan finns i listan, lägg till det
                        if (!recipesList.contains(mealId)) {
                            val updatedRecipes = recipesList.toMutableList().apply {
                                add(mealId)
                            }

                            // Uppdatera familjens "recipes" lista i Firestore
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

    // Ny metod för att ta bort ett recept från användarens familjs favoriter
    fun removeMealFromFamily(mealId: String) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Hämta UID för den inloggade användaren
            val userId = currentUser.uid

            // Hitta familjen där användaren är medlem
            db.collection("families")
                .whereArrayContains("members", userId) // Filtrera på familjer där "members" innehåller användarens UID
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val familyDocument = documents.first()
                        val familyId = familyDocument.id
                        val recipesList = familyDocument.get("recipes") as? List<String> ?: emptyList()

                        // Om receptet finns i listan, ta bort det
                        if (recipesList.contains(mealId)) {
                            val updatedRecipes = recipesList.toMutableList().apply {
                                remove(mealId) // Ta bort mealId från listan
                            }

                            // Uppdatera familjens "recipes" lista i Firestore
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


    // Ny metod för att kolla om ett recept finns i användarens familjs favoriter
    fun isMealInFamily(mealId: String, onResult: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Hämta UID för den inloggade användaren
            val userId = currentUser.uid

            // Hitta familjen där användaren är medlem
            db.collection("families")
                .whereArrayContains("members", userId) // Filtrera på familjer där "members" innehåller användarens UID
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val familyDocument = documents.first()
                        val recipesList = familyDocument.get("recipes") as? List<String> ?: emptyList()

                        // Kontrollera om mealId finns i familjens recipes-lista
                        val isMealInFamily = recipesList.contains(mealId)
                        onResult(isMealInFamily) // Skicka resultatet till callback-funktionen
                    } else {
                        onResult(false) // Om familj inte finns, returnera false
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("RecipeRepository", "Error fetching family document", e)
                    onResult(false) // Om ett fel inträffar, returnera false
                }
        } else {
            onResult(false) // Om användaren inte är inloggad, returnera false
        }
    }

}
