package com.example.save_a_strayv2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.save_a_strayv2.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun logout() {
        authRepository.logout()
    }
}
