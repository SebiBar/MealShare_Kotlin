package com.example.mealshare_kotlin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealshare_kotlin.data.api.ApiService
import com.example.mealshare_kotlin.data.auth.TokenManager
import com.example.mealshare_kotlin.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    fun getRecipeById(recipeId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getRecipeById(recipeId)
                if (response.isSuccessful) {
                    _recipe.value = response.body()
                } else {
                    _error.value = "Failed to load recipe: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error loading recipe: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteRecipe(recipeId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.deleteRecipe(recipeId)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _error.value = "Failed to delete recipe: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting recipe: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateRecipe(recipe: Recipe, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.updateRecipe(recipe.id, recipe)
                if (response.isSuccessful) {
                    _recipe.value = response.body()
                    _isEditing.value = false
                    onSuccess()
                } else {
                    _error.value = "Failed to update recipe: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error updating recipe: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleEditMode() {
        _isEditing.value = !_isEditing.value
    }

    suspend fun isCurrentUserRecipeOwner(): Boolean {
        val currentUserId = tokenManager.getUserId().first()
        return _recipe.value?.user?.id == currentUserId
    }
}
