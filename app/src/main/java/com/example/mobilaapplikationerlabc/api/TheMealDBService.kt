package com.example.mobilaapplikationerlabc.api

import com.example.mobilaapplikationerlabc.model.MealResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TheMealDBService {

    @GET("search.php")
    fun getMealsByName(@Query("s") mealName: String): Call<MealResponse>

    @GET("search.php")
    fun searchMeal(@Query("s") query: String): Call<MealResponse>

    @GET("random.php")
    fun getRandomMeal(): Call<MealResponse>

    @GET("lookup.php")
    fun getMealDetails(@Query("i") mealId: String): Call<MealResponse>
}