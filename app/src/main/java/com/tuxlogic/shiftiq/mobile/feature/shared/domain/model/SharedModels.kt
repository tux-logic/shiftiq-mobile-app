package com.tuxlogic.shiftiq.mobile.feature.shared.domain.model

data class UploadedMedia(
    val url: String
)

enum class IdentityDocumentType {
    DNI,
    RUC,
    CE;

    companion object {
        fun fromString(value: String): IdentityDocumentType = when (value.trim().uppercase()) {
            "DNI" -> DNI
            "RUC" -> RUC
            "CE", "CEE" -> CE
            else -> throw IllegalArgumentException("Unsupported document type: $value")
        }
    }
}

sealed interface IdentityDocumentData {
    val type: IdentityDocumentType
    val documentNumber: String
    val verified: Boolean
    val source: String

    data class DniData(
        override val documentNumber: String,
        val firstName: String,
        val lastName: String,
        val fullName: String,
        override val verified: Boolean = true,
        override val source: String = "RENIEC"
    ) : IdentityDocumentData {
        override val type: IdentityDocumentType = IdentityDocumentType.DNI
    }

    data class RucData(
        override val documentNumber: String,
        val businessName: String,
        val tradeName: String? = null,
        val address: String? = null,
        val condition: String? = null,
        val status: String? = null,
        override val verified: Boolean = true,
        override val source: String = "SUNAT"
    ) : IdentityDocumentData {
        override val type: IdentityDocumentType = IdentityDocumentType.RUC
    }

    data class CeData(
        override val documentNumber: String,
        val firstName: String,
        val lastName: String,
        val fullName: String,
        val nationality: String? = null,
        override val verified: Boolean = true,
        override val source: String = "MIGRACIONES"
    ) : IdentityDocumentData {
        override val type: IdentityDocumentType = IdentityDocumentType.CE
    }
}

data class SystemHealth(
    val status: String,
    val app: String? = null,
    val docs: String? = null
)
