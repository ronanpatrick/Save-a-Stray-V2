package com.example.save_a_strayv2.repository

import android.net.Uri
import com.example.save_a_strayv2.model.Pet
import com.example.save_a_strayv2.model.AdoptionStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class PetRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PetRepository {

    private val petsCollection = firestore.collection("pets")

    override suspend fun uploadImage(uri: Uri): Result<String> = suspendCancellableCoroutine { continuation ->
        val requestId = MediaManager.get().upload(uri)
            .unsigned("save_a_stray_preset")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val secureUrl = resultData["secure_url"] as? String
                    if (secureUrl != null) {
                        continuation.resume(Result.success(secureUrl))
                    } else {
                        continuation.resume(Result.failure(Exception("Cloudinary URL missing")))
                    }
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    continuation.resume(Result.failure(Exception(error.description)))
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    continuation.resume(Result.failure(Exception("Upload rescheduled: ${error.description}")))
                }
            })
            .dispatch()

        continuation.invokeOnCancellation {
            MediaManager.get().cancelRequest(requestId)
        }
    }

    override suspend fun addPet(pet: Pet): Result<Unit> {
        return try {
            // Generate a random UUID if petId is empty
            val newPetId = if (pet.petId.isBlank()) UUID.randomUUID().toString() else pet.petId
            val finalPet = pet.copy(petId = newPetId)

            petsCollection
                .document(newPetId)
                .set(finalPet)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun getAllAvailablePets(): Flow<List<Pet>> = callbackFlow {
        val listener = petsCollection
            .whereEqualTo("status", AdoptionStatus.AVAILABLE.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pets = snapshot.documents.mapNotNull { it.toObject(Pet::class.java) }
                    trySend(pets)
                }
            }

        awaitClose {
            listener.remove()
        }
    }
}
