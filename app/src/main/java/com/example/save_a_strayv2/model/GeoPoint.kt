package com.example.save_a_strayv2.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
