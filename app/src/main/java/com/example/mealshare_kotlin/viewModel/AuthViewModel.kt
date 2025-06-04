package com.example.mealshare_kotlin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealshare_kotlin.data.api.ApiClient
import com.example.mealshare_kotlin.data.auth.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.Typography.dagger

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
     * Logout function that clears the token
     */
    fun logout() {
        viewModelScope.launch {
            apiClient.clearAuthToken()
        }
    }
}
