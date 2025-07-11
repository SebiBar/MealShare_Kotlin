package com.example.mealshare_kotlin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealshare_kotlin.data.api.ApiService
import com.example.mealshare_kotlin.model.Recipe
import com.example.mealshare_kotlin.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    private val _userRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val userRecipes: StateFlow<List<Recipe>> = _userRecipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Loads a user's profile by ID
     */
    fun loadUserProfile(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                //dummy data
                _userProfile.value = User(id = userId, username = "User $userId")
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load user profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads recipes for a specific user by ID
     */
    fun loadUserRecipes(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getRecipesByUserId(userId.toInt())
                if (response.isSuccessful) {
                    val recipes = response.body() ?: emptyList()
                    _userRecipes.value = recipes

                    // Update user profile with real data from the first recipe if available
                    if (recipes.isNotEmpty()) {
                        _userProfile.value = recipes[0].user
                    }

                    _error.value = null
                } else {
                    _error.value = "Failed to load recipes: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load recipes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
