package com.example.save_a_strayv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.save_a_strayv2.model.UserRole
import com.example.save_a_strayv2.repository.AuthRepository
import com.example.save_a_strayv2.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the UI state for the progressive-disclosure registration form.
 * The user first picks [UserRole.INDIVIDUAL] or [UserRole.SHELTER],
 * then the credential fields animate into view.
 */
@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var isLoading: Boolean by mutableStateOf(false)
        private set

    // ── Step 1: Role selection ────────────────────────────────────────────────
    var selectedRole: UserRole? by mutableStateOf(null)
        private set

    // ── Step 2: Shared credential fields ─────────────────────────────────────
    var email: String by mutableStateOf("")
        private set
    var password: String by mutableStateOf("")
        private set
    var confirmPassword: String by mutableStateOf("")
        private set

    // ── Step 2: Role-branched name fields ─────────────────────────────────────
    /** Used by INDIVIDUAL */
    var name: String by mutableStateOf("")
        private set
    /** Used by SHELTER only */
    var orgName: String by mutableStateOf("")
        private set

    // ── Field visibility / error state ────────────────────────────────────────
    var passwordVisible: Boolean by mutableStateOf(false)
        private set
    var confirmPasswordVisible: Boolean by mutableStateOf(false)
        private set

    // Form-level validation error shown after submit attempt
    var formError: String? by mutableStateOf(null)
        private set

    // ── Mutators ──────────────────────────────────────────────────────────────
    fun onRoleSelected(role: UserRole) {
        selectedRole = role
        formError = null
    }

    fun onEmailChanged(value: String)           { email = value;           formError = null }
    fun onPasswordChanged(value: String)        { password = value;        formError = null }
    fun onConfirmPasswordChanged(value: String) { confirmPassword = value; formError = null }
    fun onNameChanged(value: String)            { name = value;            formError = null }
    fun onOrgNameChanged(value: String)         { orgName = value;         formError = null }

    fun togglePasswordVisibility()        { passwordVisible = !passwordVisible }
    fun toggleConfirmPasswordVisibility() { confirmPasswordVisible = !confirmPasswordVisible }

    // ── Validation ────────────────────────────────────────────────────────────
    fun validate(): Boolean {
        if (selectedRole == null) {
            formError = "Please select your account type to continue."
            return false
        }
        if (email.isBlank()) {
            formError = "Email address is required."
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            formError = "Please enter a valid email address."
            return false
        }
        if (password.length < 8) {
            formError = "Password must be at least 8 characters."
            return false
        }
        if (password != confirmPassword) {
            formError = "Passwords do not match."
            return false
        }
        when (selectedRole) {
            UserRole.INDIVIDUAL -> {
                if (name.isBlank()) {
                    formError = "Full name is required."
                    return false
                }
            }
            UserRole.SHELTER -> {
                if (orgName.isBlank()) {
                    formError = "Organization name is required."
                    return false
                }
            }
            null -> { /* handled above */ }
        }
        formError = null
        return true
    }

    /** Called when the form passes validation. Real auth logic will live in the Repository. */
    fun onSubmit() {
        if (!validate()) return
        
        isLoading = true
        formError = null
        
        viewModelScope.launch {
            val result = authRepository.register(
                email = email,
                password = password,
                role = selectedRole!!,
                name = name,
                orgName = orgName
            )
            
            isLoading = false
            when (result) {
                is AuthResult.Success -> {
                    // Registration successful.
                    // The NavHost in MainActivity will observe the Firebase Auth state change 
                    // and automatically route to the Main screen.
                }
                is AuthResult.Error -> {
                    formError = result.message
                }
            }
        }
    }
}
