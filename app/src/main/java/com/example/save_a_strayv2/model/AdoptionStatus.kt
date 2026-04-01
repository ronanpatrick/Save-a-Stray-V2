package com.example.save_a_strayv2.model

import kotlinx.serialization.Serializable

@Serializable
enum class AdoptionStatus(val displayName: String) {
    AVAILABLE("Available for Adoption"),
    PENDING("Adoption Pending"),
    ADOPTED("Adopted"),
    OWNED("Personal Pet"),
    STRAY("Stray Sighting")
}
