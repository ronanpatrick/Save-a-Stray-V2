package com.example.save_a_strayv2.repository

import android.content.Context
import android.net.Uri
import com.example.save_a_strayv2.model.Pet
import com.example.save_a_strayv2.model.AdoptionStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
    private val context: Context
) : PetRepository {

    override suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            val bytes = withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } ?: return Result.failure(Exception("Failed to read image bytes"))

            val extension = context.contentResolver.getType(uri)?.split("/")?.lastOrNull() ?: "jpg"
            val fileName = "pet_${UUID.randomUUID()}.$extension"

            val bucket = supabase.storage.from("pet-images")
            bucket.upload(fileName, bytes)
            
            val publicUrl = bucket.publicUrl(fileName)
            Result.success(publicUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun addPet(pet: Pet): Result<Unit> {
        return try {
            val newPetId = if (pet.petId.isBlank()) UUID.randomUUID().toString() else pet.petId
            val finalPet = pet.copy(petId = newPetId)

            supabase.postgrest["pets"].insert(finalPet)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getAllAvailablePets(): List<Pet> {
        return try {
            supabase.postgrest["pets"]
                .select {
                    filter {
                        eq("adoption_status", AdoptionStatus.AVAILABLE.name)
                    }
                }
                .decodeList<Pet>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
