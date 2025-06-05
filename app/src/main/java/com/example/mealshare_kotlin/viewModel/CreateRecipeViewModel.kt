package com.example.mealshare_kotlin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealshare_kotlin.data.api.ApiService
import com.example.mealshare_kotlin.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Create a new recipe
     */
    fun createRecipe(recipe: Recipe, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                val response = apiService.createRecipe(recipe)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _error.value = "Failed to create recipe: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error creating recipe: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
}
