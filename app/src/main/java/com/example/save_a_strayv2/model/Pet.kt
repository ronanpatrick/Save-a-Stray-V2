package com.example.save_a_strayv2.model

import com.google.firebase.firestore.GeoPoint

data class Pet(
    val petId: String = "",
    val ownerId: String = "",
    val name: String = "",
    val breed: String? = null,
    val location: GeoPoint? = null,
    val province: String = "",
    val city: String = "",
    val barangay: String = "",
    val vaccinationStatus: VaccinationStatus = VaccinationStatus.UNSURE,
    val personalityTraits: List<String> = emptyList(),
    val mainImageUrl: String = "",
    val galleryUrls: List<String> = emptyList(),
    val medicalNotes: String? = null,
    val status: AdoptionStatus = AdoptionStatus.AVAILABLE
)
