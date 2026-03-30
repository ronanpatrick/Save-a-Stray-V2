package com.example.save_a_strayv2.model

import com.google.firebase.firestore.GeoPoint

data class User(
    val uid: String = "",
    val email: String = "",
    val role: UserRole = UserRole.ADOPTER,
    val name: String = "",          // Used by ADOPTER and INDIVIDUAL
    val orgName: String = "",       // Used by SHELTER
    val location: GeoPoint? = null
)
