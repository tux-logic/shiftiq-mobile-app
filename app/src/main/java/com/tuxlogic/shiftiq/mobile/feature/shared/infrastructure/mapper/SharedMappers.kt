package com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.mapper

import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto.HealthResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto.IdentityValidationResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto.MediaUploadResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto.RootPingResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentData
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentType
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.SystemHealth
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.UploadedMedia

fun MediaUploadResourceDto.toDomain(): UploadedMedia = UploadedMedia(
    url = url
)

fun IdentityValidationResourceDto.toDomain(): IdentityDocumentData {
    val docType = runCatching { IdentityDocumentType.fromString(type) }
        .getOrDefault(IdentityDocumentType.DNI)

    return when (docType) {
        IdentityDocumentType.DNI -> IdentityDocumentData.DniData(
            documentNumber = documentNumber,
            firstName = firstName ?: "",
            lastName = lastName ?: "",
            fullName = fullName ?: listOfNotNull(firstName, lastName).joinToString(" "),
            verified = verified ?: true,
            source = source ?: "RENIEC"
        )
        IdentityDocumentType.RUC -> IdentityDocumentData.RucData(
            documentNumber = documentNumber,
            businessName = businessName ?: fullName ?: "",
            tradeName = tradeName,
            address = address,
            condition = condition,
            status = status,
            verified = verified ?: true,
            source = source ?: "SUNAT"
        )
        IdentityDocumentType.CE -> IdentityDocumentData.CeData(
            documentNumber = documentNumber,
            firstName = firstName ?: "",
            lastName = lastName ?: "",
            fullName = fullName ?: listOfNotNull(firstName, lastName).joinToString(" "),
            nationality = nationality,
            verified = verified ?: true,
            source = source ?: "MIGRACIONES"
        )
    }
}

fun RootPingResourceDto.toDomain(): SystemHealth = SystemHealth(
    status = status,
    app = app,
    docs = docs
)

fun HealthResourceDto.toDomain(): SystemHealth = SystemHealth(
    status = status
)
