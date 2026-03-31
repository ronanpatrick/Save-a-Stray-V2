package com.example.save_a_strayv2.repository

import android.net.Uri
import com.example.save_a_strayv2.model.Pet
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    suspend fun addPet(pet: Pet): Result<Unit>
    suspend fun uploadImage(uri: Uri): Result<String>
    fun getAllAvailablePets(): Flow<List<Pet>>
}
