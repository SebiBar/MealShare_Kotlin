package com.example.mealshare_kotlin.data.repository

import com.example.mealshare_kotlin.data.api.ApiClient
import com.example.mealshare_kotlin.data.api.ApiService
import com.example.mealshare_kotlin.data.auth.TokenManager
import com.example.mealshare_kotlin.model.UserAuthResponse
import com.example.mealshare_kotlin.model.UserLogin
import com.example.mealshare_kotlin.model.UserRegister
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiClient: ApiClient,
    private val tokenManager: TokenManager
) {
    suspend fun login(username: String, password: String): Flow<Result<UserAuthResponse>> = flow {
        try {
            val userLogin = UserLogin(username, password)
            val response = apiService.loginUser(userLogin)

            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    // Save both token and user data
                    apiClient.setAuthData(authResponse)
                    emit(Result.success(authResponse))
                } ?: emit(Result.failure(Exception("Empty response body")))
            } else {
                emit(Result.failure(Exception("Login failed: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun register(email: String, username: String, password: String): Flow<Result<UserAuthResponse>> = flow {
        try {
            val userRegister = UserRegister(email, username, password)
            val response = apiService.registerUser(userRegister)

            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    // Save both token and user data
                    apiClient.setAuthData(authResponse)
                    emit(Result.success(authResponse))
                } ?: emit(Result.failure(Exception("Empty response body")))
            } else {
                emit(Result.failure(Exception("Registration failed: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun logout() {
        // Clear the token
        tokenManager.clearToken()
    }
}
