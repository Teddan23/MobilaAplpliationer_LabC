package com.example.mobilaapplikationerlabc.api

import com.example.mobilaapplikationerlabc.model.Meal
import com.example.mobilaapplikationerlabc.model.MealResponse

fun mapToMeal(mealResponse: MealResponse): Meal? {
    val apiMeal = mealResponse.meals.firstOrNull() ?: return null

    // Använd reflexion för att hämta ingredienser och mått
    val ingredients = (1..20).mapNotNull { index ->
        try {
            val field = apiMeal.javaClass.getDeclaredField("strIngredient$index")
            field.isAccessible = true
            field.get(apiMeal) as? String
        } catch (e: NoSuchFieldException) {
            null
        }
    }

    val measures = (1..20).mapNotNull { index ->
        try {
            val field = apiMeal.javaClass.getDeclaredField("strMeasure$index")
            field.isAccessible = true
            field.get(apiMeal) as? String
        } catch (e: NoSuchFieldException) {
            null
        }
    }

    return Meal(
        idMeal = apiMeal.idMeal,
        strMeal = apiMeal.strMeal,
        strCategory = apiMeal.strCategory,
        strInstructions = apiMeal.strInstructions,
        strMealThumb = apiMeal.strMealThumb,
        strTags = apiMeal.strTags,
        strYoutube = apiMeal.strYoutube,
        // Returnera ingredienser och mått som individuella fält
        strIngredient1 = ingredients.getOrElse(0) { "" },
        strIngredient2 = ingredients.getOrElse(1) { "" },
        strIngredient3 = ingredients.getOrElse(2) { "" },
        strIngredient4 = ingredients.getOrElse(3) { "" },
        strIngredient5 = ingredients.getOrElse(4) { "" },
        strIngredient6 = ingredients.getOrElse(5) { "" },
        strIngredient7 = ingredients.getOrElse(6) { "" },
        strIngredient8 = ingredients.getOrElse(7) { "" },
        strIngredient9 = ingredients.getOrElse(8) { "" },
        strIngredient10 = ingredients.getOrElse(9) { "" },
        strIngredient11 = ingredients.getOrElse(10) { "" },
        strIngredient12 = ingredients.getOrElse(11) { "" },
        strIngredient13 = ingredients.getOrElse(12) { "" },
        strIngredient14 = ingredients.getOrElse(13) { "" },
        strIngredient15 = ingredients.getOrElse(14) { "" },
        strIngredient16 = ingredients.getOrElse(15) { "" },
        strIngredient17 = ingredients.getOrElse(16) { "" },
        strIngredient18 = ingredients.getOrElse(17) { "" },
        strIngredient19 = ingredients.getOrElse(18) { "" },
        strIngredient20 = ingredients.getOrElse(19) { "" },
        strMeasure1 = measures.getOrElse(0) { "" },
        strMeasure2 = measures.getOrElse(1) { "" },
        strMeasure3 = measures.getOrElse(2) { "" },
        strMeasure4 = measures.getOrElse(3) { "" },
        strMeasure5 = measures.getOrElse(4) { "" },
        strMeasure6 = measures.getOrElse(5) { "" },
        strMeasure7 = measures.getOrElse(6) { "" },
        strMeasure8 = measures.getOrElse(7) { "" },
        strMeasure9 = measures.getOrElse(8) { "" },
        strMeasure10 = measures.getOrElse(9) { "" },
        strMeasure11 = measures.getOrElse(10) { "" },
        strMeasure12 = measures.getOrElse(11) { "" },
        strMeasure13 = measures.getOrElse(12) { "" },
        strMeasure14 = measures.getOrElse(13) { "" },
        strMeasure15 = measures.getOrElse(14) { "" },
        strMeasure16 = measures.getOrElse(15) { "" },
        strMeasure17 = measures.getOrElse(16) { "" },
        strMeasure18 = measures.getOrElse(17) { "" },
        strMeasure19 = measures.getOrElse(18) { "" },
        strMeasure20 = measures.getOrElse(19) { "" }
    )
}