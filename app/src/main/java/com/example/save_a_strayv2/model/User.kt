package com.example.save_a_strayv2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("role") val role: UserRole = UserRole.INDIVIDUAL,
    @SerialName("name") val name: String = ""
)
