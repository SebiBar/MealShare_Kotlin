package com.example.mealshare_kotlin.model

data class UserRegister(
    val email: String,
    val username: String,
    val password: String
)

data class UserLogin(
    val username: String,
    val password: String
)

data class UserAuthResponse(
    val user: User,
    val token: String,
)