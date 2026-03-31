package com.example.save_a_strayv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.save_a_strayv2.repository.AuthRepository
import com.example.save_a_strayv2.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the login form state and delegates authentication to [AuthRepository].
 * On success, the RootNavGraph's auth-state listener automatically navigates to Main.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email: String by mutableStateOf("")
        private set
    var password: String by mutableStateOf("")
        private set

    var passwordVisible: Boolean by mutableStateOf(false)
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set
    var formError: String? by mutableStateOf(null)
        private set

    // ── Mutators ──────────────────────────────────────────────────────────────
    fun onEmailChanged(value: String)    { email = value;    formError = null }
    fun onPasswordChanged(value: String) { password = value; formError = null }
    fun togglePasswordVisibility()       { passwordVisible = !passwordVisible }

    // ── Validation ────────────────────────────────────────────────────────────
    private fun validate(): Boolean {
        if (email.isBlank()) {
            formError = "Email address is required."
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            formError = "Please enter a valid email address."
            return false
        }
        if (password.isBlank()) {
            formError = "Password is required."
            return false
        }
        return true
    }

    // ── Submit ────────────────────────────────────────────────────────────────
    fun onSubmit() {
        if (!validate()) return

        isLoading = true
        formError = null

        viewModelScope.launch {
            val result = authRepository.login(email, password)

            isLoading = false
            when (result) {
                is AuthResult.Success -> {
                    // Auth state listener in RootNavGraph handles navigation.
                }
                is AuthResult.Error -> {
                    formError = result.message
                }
            }
        }
    }
}
