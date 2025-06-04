package com.example.mealshare_kotlin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealshare_kotlin.data.api.ApiClient
import com.example.mealshare_kotlin.data.auth.TokenManager
import com.example.mealshare_kotlin.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel that manages authentication state across the app
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiClient: ApiClient,
    private val tokenManager: TokenManager
) : ViewModel() {

    /**
     * Flow that emits authentication status based on token existence
     */
    val isAuthenticated: Flow<Boolean> = tokenManager.getToken().map { token ->
        !token.isNullOrBlank()
    }

    /**
     * Flow that emits the current user data
     */
    val currentUser: Flow<User?> = tokenManager.getUser()

    /**
     * Get current user synchronously (might return null if not loaded yet)
     */
    fun getCurrentUser(): User? {
        return apiClient.getCurrentUser()
    }

    /**
     * Logout function that clears the token and user data
     */
    fun logout() {
        viewModelScope.launch {
            apiClient.clearAuthToken()
        }
    }
}
