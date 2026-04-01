package com.example.save_a_strayv2.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.save_a_strayv2.model.AdoptionStatus
import com.example.save_a_strayv2.model.Pet
import com.example.save_a_strayv2.model.VaccinationStatus
import com.example.save_a_strayv2.repository.AuthRepository
import com.example.save_a_strayv2.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPetViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var name: String by mutableStateOf("")
        private set
    var breed: String by mutableStateOf("")
        private set
    var vaccinationStatus: VaccinationStatus by mutableStateOf(VaccinationStatus.UNSURE)
        private set
    var personalityTraits: String by mutableStateOf("")
        private set
    var medicalNotes: String by mutableStateOf("")
        private set
    var province: String by mutableStateOf("")
        private set
    var city: String by mutableStateOf("")
        private set
    var barangay: String by mutableStateOf("")
        private set
    var adoptionStatus: AdoptionStatus by mutableStateOf(AdoptionStatus.AVAILABLE)
        private set

    var mainImageUri: Uri? by mutableStateOf(null)
        private set
    var galleryUris: List<Uri> by mutableStateOf(emptyList())
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set
    var formError: String? by mutableStateOf(null)
        private set
    var isSuccess: Boolean by mutableStateOf(false)
        private set

    fun onNameChanged(value: String)              { name = value;              formError = null }
    fun onBreedChanged(value: String)             { breed = value;             formError = null }
    fun onVaccinationStatusChanged(value: VaccinationStatus) { vaccinationStatus = value; formError = null }
    fun onPersonalityTraitsChanged(value: String) { personalityTraits = value; formError = null }
    fun onMedicalNotesChanged(value: String)      { medicalNotes = value;      formError = null }
    fun onProvinceChanged(value: String)          { province = value;          formError = null }
    fun onCityChanged(value: String)              { city = value;              formError = null }
    fun onBarangayChanged(value: String)          { barangay = value;          formError = null }
    fun onAdoptionStatusChanged(value: AdoptionStatus) { adoptionStatus = value; formError = null }
    fun onMainImageSelected(uri: Uri?)            { mainImageUri = uri;        formError = null }
    fun onGalleryImagesSelected(uris: List<Uri>)  { galleryUris = uris;        formError = null }

    private fun validate(): Boolean {
        if (name.isBlank()) {
            formError = "Pet name is required."
            return false
        }
        if (province.isBlank() || city.isBlank() || barangay.isBlank()) {
            formError = "Province, City, and Barangay are required."
            return false
        }
        return true
    }

    fun onSubmit() {
        if (!validate()) return

        val currentUserUid = authRepository.currentUser?.id
        if (currentUserUid == null) {
            formError = "You must be signed in to register an animal."
            return
        }

        isLoading = true
        formError = null

        val traitsList = personalityTraits
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val finalBreed = breed.trim().takeIf { it.isNotBlank() }
        val finalMedicalNotes = medicalNotes.trim().takeIf { it.isNotBlank() }

        viewModelScope.launch {
            try {
                // Upload main image
                var finalMainImageUrl = ""
                if (mainImageUri != null) {
                    val mainUpload = petRepository.uploadImage(mainImageUri!!)
                    if (mainUpload.isFailure) {
                        formError = "Failed to upload Profile Picture."
                        isLoading = false
                        return@launch
                    }
                    finalMainImageUrl = mainUpload.getOrThrow()
                }

                // Upload gallery images
                val finalGalleryUrls = mutableListOf<String>()
                for (uri in galleryUris) {
                    val galleryUpload = petRepository.uploadImage(uri)
                    if (galleryUpload.isFailure) {
                        formError = "Failed to upload one of the additional photos."
                        isLoading = false
                        return@launch
                    }
                    finalGalleryUrls.add(galleryUpload.getOrThrow())
                }

                // FIXED MAPPING BELOW
                val newPet = Pet(
                    ownerId = currentUserUid,
                    name = name.trim(),
                    breed = finalBreed,
                    province = province.trim(),
                    city = city.trim(),
                    barangay = barangay.trim(),
                    vaccinationStatus = vaccinationStatus,
                    personalityTraits = traitsList,
                    medicalNotes = finalMedicalNotes,
                    adoptionStatus = adoptionStatus,
                    mainImageUrl = finalMainImageUrl,
                    gallery_urls = finalGalleryUrls // Changed from galleryUrls to gallery_urls
                )

                val result = petRepository.addPet(newPet)

                result.onSuccess {
                    isSuccess = true
                }.onFailure { exception ->
                    formError = exception.localizedMessage ?: "Failed to add pet schema."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                formError = e.localizedMessage ?: "An unexpected error occurred."
            } finally {
                isLoading = false
            }
        }
    }
}