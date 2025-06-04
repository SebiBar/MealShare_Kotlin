package com.example.mealshare_kotlin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealshare_kotlin.data.api.ApiService
import com.example.mealshare_kotlin.model.Search
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _searchResults = MutableStateFlow<Search?>(null)
    val searchResults: StateFlow<Search?> = _searchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = null
            return
        }

        viewModelScope.launch {
            try {
                _isSearching.value = true
                val response = apiService.search(query)
                if (response.isSuccessful) {
                    _searchResults.value = response.body()
                } else {
                    // Handle error
                    _searchResults.value = null
                }
            } catch (e: Exception) {
                // Handle exception
                _searchResults.value = null
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = null
    }
}
