package com.example.save_a_strayv2.repository

import android.net.Uri
import com.example.save_a_strayv2.model.Pet

interface PetRepository {
    suspend fun addPet(pet: Pet): Result<Unit>
    suspend fun uploadImage(uri: Uri): Result<String>
    suspend fun getAllAvailablePets(): List<Pet>
}
