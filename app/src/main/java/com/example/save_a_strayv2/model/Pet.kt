package com.example.save_a_strayv2.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pet(
    @SerialName("id") val petId: String = "",
    @SerialName("owner_id") val ownerId: String = "",
    val name: String = "",
    val breed: String? = null,
    val province: String = "",
    val city: String = "",
    val barangay: String = "",

    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    @SerialName("vaccination_status") val vaccinationStatus: VaccinationStatus = VaccinationStatus.UNSURE,

    @SerialName("personality_traits") val personalityTraits: List<String> = emptyList(),
    @SerialName("main_image_url") val mainImageUrl: String = "",

    // The sledgehammer fix: Direct snake_case variable name
    val gallery_urls: List<String> = emptyList(),

    @SerialName("medical_notes") val medicalNotes: String? = null,

    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    @SerialName("adoption_status") val adoptionStatus: AdoptionStatus = AdoptionStatus.AVAILABLE
)