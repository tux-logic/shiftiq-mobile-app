package com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

data class MediaUploadResourceDto(
    @SerializedName("url")
    val url: String
)

data class IdentityValidationResourceDto(
    @SerializedName("type")
    val type: String,
    @SerializedName("documentNumber")
    val documentNumber: String,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("fullName")
    val fullName: String? = null,
    @SerializedName("businessName")
    val businessName: String? = null,
    @SerializedName("tradeName")
    val tradeName: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("condition")
    val condition: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("nationality")
    val nationality: String? = null,
    @SerializedName("verified")
    val verified: Boolean? = true,
    @SerializedName("source")
    val source: String? = null
)

data class RootPingResourceDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("app")
    val app: String? = null,
    @SerializedName("docs")
    val docs: String? = null
)

data class HealthResourceDto(
    @SerializedName("status")
    val status: String
)
