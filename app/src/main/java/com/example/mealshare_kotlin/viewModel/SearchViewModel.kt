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

    // Control whether to show search results dropdown
    private val _showSearchResults = MutableStateFlow(false)
    val showSearchResults: StateFlow<Boolean> = _showSearchResults

    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = null
            _showSearchResults.value = false
            return
        }

        viewModelScope.launch {
            try {
                _isSearching.value = true
                val response = apiService.search(query)
                if (response.isSuccessful) {
                    _searchResults.value = response.body()
                    // Show search results dropdown if we have results
                    _showSearchResults.value = response.body() != null &&
                            (response.body()?.recipes?.isNotEmpty() == true ||
                             response.body()?.users?.isNotEmpty() == true)
                } else {
                    // Handle error
                    _searchResults.value = null
                    _showSearchResults.value = false
                }
            } catch (e: Exception) {
                // Handle exception
                _searchResults.value = null
                _showSearchResults.value = false
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = null
        _showSearchResults.value = false
    }

    fun clearSearchResults() {
        _showSearchResults.value = false
    }
}
