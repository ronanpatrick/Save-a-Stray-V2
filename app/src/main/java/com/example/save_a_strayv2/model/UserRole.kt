package com.example.save_a_strayv2.model

enum class UserRole(val displayName: String, val description: String) {
    ADOPTER("Adopter", "Looking to give a pet a forever home"),
    INDIVIDUAL("Individual", "A private owner rehoming a personal pet"),
    SHELTER("Shelter", "An organization managing multiple animals")
}
