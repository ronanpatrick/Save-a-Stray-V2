package com.example.save_a_strayv2.model

enum class UserRole(val displayName: String, val description: String) {
    INDIVIDUAL("Personal Account", "Adopt or rehome pets as an individual"),
    SHELTER("Shelter / Rescue", "An organization managing multiple animals")
}
