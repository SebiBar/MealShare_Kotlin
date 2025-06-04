package com.example.mealshare_kotlin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealshare_kotlin.data.repository.AuthRepository
import com.example.mealshare_kotlin.model.UserAuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.Typography.dagger

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Input fields state
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // Update username input
    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    // Update password input
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    // Perform login
    fun login() {
        val currentUsername = _username.value
        val currentPassword = _password.value

        if (currentUsername.isBlank() || currentPassword.isBlank()) {
            _uiState.value = LoginUiState.Error("Username and password cannot be empty")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            authRepository.login(currentUsername, currentPassword).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = LoginUiState.Success(response)
                    },
                    onFailure = { exception ->
                        _uiState.value = LoginUiState.Error(exception.message ?: "Login failed")
                    }
                )
            }
        }
    }

    // Clear login errors
    fun clearError() {
        if (_uiState.value is LoginUiState.Error) {
            _uiState.value = LoginUiState.Initial
        }
    }
}

// Sealed class representing different states of the login process
sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val data: UserAuthResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
