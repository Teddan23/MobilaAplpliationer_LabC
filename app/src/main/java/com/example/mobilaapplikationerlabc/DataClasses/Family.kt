package com.example.mobilaapplikationerlabc.DataClasses

data class Family(
    val name: String = "",
    val members: List<String> = listOf(),
    val recipes: List<String> = listOf(),
    val shoppingList: List<String> = listOf()
)