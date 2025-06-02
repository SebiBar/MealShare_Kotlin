package com.example.mealshare_kotlin.data.api

import com.example.mealshare_kotlin.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Authentication endpoints
    @POST("auth/register")
    suspend fun registerUser(@Body userRegister: UserRegister): Response<UserAuthResponse>

    @POST("auth/login")
    suspend fun loginUser(@Body userLogin: UserLogin): Response<UserAuthResponse>

    // Recipe endpoints
    @GET("users/{id}/recipes")
    suspend fun getRecipesByUserId(@Path("id") userId: Int): Response<List<Recipe>>

    @GET("recipes/{id}")
    suspend fun getRecipeById(@Path("id") recipeId: Long): Response<Recipe>

    @POST("recipes")
    suspend fun createRecipe(@Body recipe: Recipe): Response<Recipe>

    @PUT("recipes/{id}")
    suspend fun updateRecipe(@Path("id") recipeId: Long, @Body recipe: Recipe): Response<Recipe>

    @DELETE("recipes/{id}")
    suspend fun deleteRecipe(@Path("id") recipeId: Long): Response<Unit>

    // Search endpoint
    @GET("search")
    suspend fun search(@Query("query") searchQuery: String): Response<Search>
}
