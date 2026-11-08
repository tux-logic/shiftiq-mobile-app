package com.tuxlogic.shiftiq.mobile.feature.shared

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.api.HealthApiService
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.api.IdentityValidationApiService
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.api.MediaApiService
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto.HealthResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto.IdentityValidationResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto.MediaUploadResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto.RootPingResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.repository.HealthRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.repository.IdentityValidationRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.repository.MediaRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentData
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import retrofit2.Response

class SharedRepositoriesTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val mediaApiService: MediaApiService = mockk()
    private val identityValidationApiService: IdentityValidationApiService = mockk()
    private val healthApiService: HealthApiService = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    private lateinit var mediaRepository: MediaRepositoryImpl
    private lateinit var identityValidationRepository: IdentityValidationRepositoryImpl
    private lateinit var healthRepository: HealthRepositoryImpl

    @Before
    fun setUp() {
        mediaRepository = MediaRepositoryImpl(mediaApiService, dispatchers)
        identityValidationRepository = IdentityValidationRepositoryImpl(identityValidationApiService, dispatchers)
        healthRepository = HealthRepositoryImpl(healthApiService, dispatchers)
    }

    // === MediaRepository ===

    @Test
    fun `uploadImage with File calls mediaApiService and returns UploadedMedia`() = runTest(testDispatcher) {
        val file = tempFolder.newFile("sample.jpg").apply {
            writeBytes("fake-image-bytes".toByteArray())
        }
        val dto = MediaUploadResourceDto("https://cloudinary.com/test.jpg")
        coEvery { mediaApiService.uploadImage(any(), any()) } returns Response.success(dto)

        val result = mediaRepository.uploadImage(file, "vehicles")
        assertTrue(result is AppResult.Success)
        assertEquals("https://cloudinary.com/test.jpg", (result as AppResult.Success).data.url)
    }

    @Test
    fun `uploadImage with ByteArray calls mediaApiService and returns UploadedMedia`() = runTest(testDispatcher) {
        val bytes = "fake-binary".toByteArray()
        val dto = MediaUploadResourceDto("https://cloudinary.com/test2.png")
        coEvery { mediaApiService.uploadImage(any(), any()) } returns Response.success(dto)

        val result = mediaRepository.uploadImage(bytes, "photo.png", "image/png", "inspections")
        assertTrue(result is AppResult.Success)
        assertEquals("https://cloudinary.com/test2.png", (result as AppResult.Success).data.url)
    }

    // === IdentityValidationRepository ===

    @Test
    fun `lookupDocument with DNI calls identityValidationApiService and returns DniData`() = runTest(testDispatcher) {
        val dto = IdentityValidationResourceDto(
            type = "DNI",
            documentNumber = "72345678",
            fullName = "Juan Carlos Perez",
            firstName = "Juan Carlos",
            lastName = "Perez",
            verified = true,
            source = "RENIEC"
        )
        coEvery { identityValidationApiService.lookupDocument("DNI", "72345678") } returns Response.success(dto)

        val result = identityValidationRepository.lookupDocument(IdentityDocumentType.DNI, "72345678")
        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertTrue(data is IdentityDocumentData.DniData)
        val dni = data as IdentityDocumentData.DniData
        assertEquals("72345678", dni.documentNumber)
        assertEquals("Juan Carlos Perez", dni.fullName)
    }

    @Test
    fun `lookupDocument with RUC calls identityValidationApiService and returns RucData`() = runTest(testDispatcher) {
        val dto = IdentityValidationResourceDto(
            type = "RUC",
            documentNumber = "20601234567",
            fullName = "TuxLogic Motors SAC",
            businessName = "TuxLogic Motors SAC",
            tradeName = "TuxLogic",
            address = "Av Benavides 123",
            condition = "HABIDO",
            status = "ACTIVO",
            verified = true,
            source = "SUNAT"
        )
        coEvery { identityValidationApiService.lookupDocument("RUC", "20601234567") } returns Response.success(dto)

        val result = identityValidationRepository.lookupDocument(IdentityDocumentType.RUC, "20601234567")
        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertTrue(data is IdentityDocumentData.RucData)
        val ruc = data as IdentityDocumentData.RucData
        assertEquals("20601234567", ruc.documentNumber)
        assertEquals("TuxLogic Motors SAC", ruc.businessName)
    }

    @Test
    fun `lookupDocument with CE calls identityValidationApiService and returns CeData`() = runTest(testDispatcher) {
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
        coEvery { identityValidationApiService.lookupDocument("CE", "001234567") } returns Response.success(dto)

        val result = identityValidationRepository.lookupDocument(IdentityDocumentType.CE, "001234567")
        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertTrue(data is IdentityDocumentData.CeData)
        val ce = data as IdentityDocumentData.CeData
        assertEquals("001234567", ce.documentNumber)
        assertEquals("VENEZOLANA", ce.nationality)
    }

    // === HealthRepository ===

    @Test
    fun `getRootPing calls healthApiService and returns SystemHealth`() = runTest(testDispatcher) {
        val dto = RootPingResourceDto(
            status = "UP",
            app = "shiftiq-backend",
            docs = "/api/v1/swagger-ui.html"
        )
        coEvery { healthApiService.getRootPing() } returns Response.success(dto)

        val result = healthRepository.getRootPing()
        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals("UP", data.status)
        assertEquals("shiftiq-backend", data.app)
        assertEquals("/api/v1/swagger-ui.html", data.docs)
    }

    @Test
    fun `getHealth calls healthApiService and returns SystemHealth`() = runTest(testDispatcher) {
        val dto = HealthResourceDto(status = "UP")
        coEvery { healthApiService.getHealth() } returns Response.success(dto)

        val result = healthRepository.getHealth()
        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals("UP", data.status)
    }
}
