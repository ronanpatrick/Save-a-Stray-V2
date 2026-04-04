package com.example.save_a_strayv2.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path

@Serializable
data class Province(
    val code: String,
    val name: String
)

@Serializable
data class City(
    val code: String,
    val name: String
)

@Serializable
data class Barangay(
    val code: String,
    val name: String
)

interface PhLocationApi {

    @GET("provinces")
    suspend fun getProvinces(): List<Province>

    @GET("provinces/{provinceCode}/cities-municipalities")
    suspend fun getCitiesByProvince(
        @Path("provinceCode") provinceCode: String
    ): List<City>

    @GET("cities-municipalities/{cityCode}/barangays")
    suspend fun getBarangaysByCity(
        @Path("cityCode") cityCode: String
    ): List<Barangay>
}
