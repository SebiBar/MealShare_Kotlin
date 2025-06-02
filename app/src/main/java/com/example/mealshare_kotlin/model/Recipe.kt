package com.example.mealshare_kotlin.model

data class Recipe(
    val id: Long,
    val title: String,
    val description: String,
    val link: Any?,
    val prepTime: Long,
    val cookTime: Long,
    val servingSize: Long,
    val calories: Long,
    val protein: Long,
    val carbs: Long,
    val fat: Long,
    val ingredients: List<Ingredient>,
    val user: User,
)

data class Ingredient(
    val id: Long,
    val name: String,
    val quantity: String,
    val unit: String,
)
