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

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Initial)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // Input fields state
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    // Update email input
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    // Update username input
    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    // Update password input
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    // Update confirm password input
    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    // Perform registration
    fun register() {
        val currentEmail = _email.value
        val currentUsername = _username.value
        val currentPassword = _password.value
        val currentConfirmPassword = _confirmPassword.value

        // Basic validation
        if (currentEmail.isBlank() || currentUsername.isBlank() || currentPassword.isBlank()) {
            _uiState.value = RegisterUiState.Error("All fields are required")
            return
        }

        if (!isValidEmail(currentEmail)) {
            _uiState.value = RegisterUiState.Error("Please enter a valid email")
            return
        }

        if (currentPassword != currentConfirmPassword) {
            _uiState.value = RegisterUiState.Error("Passwords do not match")
            return
        }

        if (currentPassword.length < 6) {
            _uiState.value = RegisterUiState.Error("Password must be at least 6 characters long")
            return
        }

        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            authRepository.register(currentEmail, currentUsername, currentPassword).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = RegisterUiState.Success(response)
                    },
                    onFailure = { exception ->
                        _uiState.value = RegisterUiState.Error(exception.message ?: "Registration failed")
                    }
                )
            }
        }
    }

    // Clear registration errors
    fun clearError() {
        if (_uiState.value is RegisterUiState.Error) {
            _uiState.value = RegisterUiState.Initial
        }
    }

    // Simple email validation
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

// Sealed class representing different states of the registration process
sealed class RegisterUiState {
    object Initial : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val data: UserAuthResponse) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
