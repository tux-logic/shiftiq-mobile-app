package com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateServiceRequestDto(
    @SerializedName("branchId") val branchId: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double
)

data class UpdateServiceRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double
)

data class ServiceResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double
)
