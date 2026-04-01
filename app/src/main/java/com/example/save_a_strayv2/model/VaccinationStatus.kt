package com.example.save_a_strayv2.model

import kotlinx.serialization.Serializable

@Serializable
enum class VaccinationStatus(val displayName: String) {
    VACCINATED("Vaccinated"),
    NOT_VACCINATED("Not Vaccinated"),
    UNSURE("Unsure")
}
