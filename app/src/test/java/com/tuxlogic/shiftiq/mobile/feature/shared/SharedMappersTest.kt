package com.tuxlogic.shiftiq.mobile.feature.shared

import com.tuxlogic.shiftiq.mobile.feature.shared.data.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.dto.HealthResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.dto.IdentityValidationResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.dto.MediaUploadResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.dto.RootPingResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SharedMappersTest {

    @Test
    fun `MediaUploadResourceDto maps correctly to domain UploadedMedia`() {
        val dto = MediaUploadResourceDto(
            url = "https://res.cloudinary.com/shiftiq/image/upload/v1234/test.jpg"
        )
        val domain = dto.toDomain()
        assertEquals("https://res.cloudinary.com/shiftiq/image/upload/v1234/test.jpg", domain.url)
    }

    @Test
    fun `IdentityValidationResourceDto for DNI maps correctly to DniData`() {
        val dto = IdentityValidationResourceDto(
            type = "DNI",
            documentNumber = "72345678",
            fullName = "Juan Carlos Perez Lopez",
            firstName = "Juan Carlos",
            lastName = "Perez Lopez",
            verified = true,
            source = "RENIEC"
        )
        val domain = dto.toDomain()
        assertTrue(domain is IdentityDocumentData.DniData)
        val dni = domain as IdentityDocumentData.DniData
        assertEquals("72345678", dni.documentNumber)
        assertEquals("Juan Carlos", dni.firstName)
        assertEquals("Perez Lopez", dni.lastName)
        assertEquals("Juan Carlos Perez Lopez", dni.fullName)
        assertTrue(dni.verified)
        assertEquals("RENIEC", dni.source)
    }

    @Test
    fun `IdentityValidationResourceDto for RUC maps correctly to RucData`() {
        val dto = IdentityValidationResourceDto(
            type = "RUC",
            documentNumber = "20601234567",
            fullName = "TuxLogic Motors S.A.C.",
            businessName = "TuxLogic Motors S.A.C.",
            tradeName = "TuxLogic",
            address = "Av Javier Prado Este 1234, San Isidro, Lima",
            condition = "HABIDO",
            status = "ACTIVO",
            verified = true,
            source = "SUNAT"
        )
        val domain = dto.toDomain()
        assertTrue(domain is IdentityDocumentData.RucData)
        val ruc = domain as IdentityDocumentData.RucData
        assertEquals("20601234567", ruc.documentNumber)
        assertEquals("TuxLogic Motors S.A.C.", ruc.businessName)
        assertEquals("TuxLogic", ruc.tradeName)
        assertEquals("Av Javier Prado Este 1234, San Isidro, Lima", ruc.address)
        assertEquals("HABIDO", ruc.condition)
        assertEquals("ACTIVO", ruc.status)
        assertTrue(ruc.verified)
        assertEquals("SUNAT", ruc.source)
    }

    @Test
    fun `IdentityValidationResourceDto for CE maps correctly to CeData`() {
        val dto = IdentityValidationResourceDto(
            type = "CE",
            documentNumber = "001234567",
            fullName = "Carlos Alberto Morales",
            firstName = "Carlos Alberto",
            lastName = "Morales",
            nationality = "VENEZOLANA",
            verified = true,
            source = "MIGRACIONES"
        )
        val domain = dto.toDomain()
        assertTrue(domain is IdentityDocumentData.CeData)
        val ce = domain as IdentityDocumentData.CeData
        assertEquals("001234567", ce.documentNumber)
        assertEquals("Carlos Alberto", ce.firstName)
        assertEquals("Morales", ce.lastName)
        assertEquals("Carlos Alberto Morales", ce.fullName)
        assertEquals("VENEZOLANA", ce.nationality)
        assertTrue(ce.verified)
        assertEquals("MIGRACIONES", ce.source)
    }

    @Test
    fun `RootPingResourceDto maps correctly to domain SystemHealth`() {
        val dto = RootPingResourceDto(
            status = "UP",
            app = "shiftiq-backend",
            docs = "/api/v1/swagger-ui.html"
        )
        val domain = dto.toDomain()
        assertEquals("UP", domain.status)
        assertEquals("shiftiq-backend", domain.app)
        assertEquals("/api/v1/swagger-ui.html", domain.docs)
    }

    @Test
    fun `HealthResourceDto maps correctly to domain SystemHealth`() {
        val dto = HealthResourceDto(
            status = "UP"
        )
        val domain = dto.toDomain()
        assertEquals("UP", domain.status)
    }
}
