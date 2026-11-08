package com.tuxlogic.shiftiq.mobile.feature.shared

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentData
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.IdentityDocumentType
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.SystemHealth
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.UploadedMedia
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.HealthRepository
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.IdentityValidationRepository
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.MediaRepository
import com.tuxlogic.shiftiq.mobile.feature.shared.application.usecase.CheckHealthUseCase
import com.tuxlogic.shiftiq.mobile.feature.shared.application.usecase.LookupIdentityDocumentUseCase
import com.tuxlogic.shiftiq.mobile.feature.shared.application.usecase.UploadImageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SharedUseCasesTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val mediaRepository: MediaRepository = mockk()
    private val identityValidationRepository: IdentityValidationRepository = mockk()
    private val healthRepository: HealthRepository = mockk()

    private lateinit var uploadImageUseCase: UploadImageUseCase
    private lateinit var lookupIdentityDocumentUseCase: LookupIdentityDocumentUseCase
    private lateinit var checkHealthUseCase: CheckHealthUseCase

    @Before
    fun setUp() {
        uploadImageUseCase = UploadImageUseCase(mediaRepository)
        lookupIdentityDocumentUseCase = LookupIdentityDocumentUseCase(identityValidationRepository)
        checkHealthUseCase = CheckHealthUseCase(healthRepository)
    }

    // === UploadImageUseCase (File) ===

    @Test
    fun `uploadImage with non-existent file returns validation error`() = runTest {
        val nonExistentFile = File(tempFolder.root, "not-found.jpg")
        val result = uploadImageUseCase(nonExistentFile)
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("file", (error as AppError.Validation).details)
    }

    @Test
    fun `uploadImage with empty file returns validation error`() = runTest {
        val emptyFile = tempFolder.newFile("empty.jpg")
        val result = uploadImageUseCase(emptyFile)
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("file", (error as AppError.Validation).details)
    }

    @Test
    fun `uploadImage with unsupported extension returns validation error`() = runTest {
        val exeFile = tempFolder.newFile("malware.exe").apply {
            writeBytes("hello".toByteArray())
        }
        val result = uploadImageUseCase(exeFile)
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("file", (error as AppError.Validation).details)
    }

    @Test
    fun `uploadImage with valid file calls mediaRepository`() = runTest {
        val validFile = tempFolder.newFile("photo.jpg").apply {
            writeBytes("sample-image-content".toByteArray())
        }
        val expected = UploadedMedia("https://cloudinary.com/test.jpg")
        coEvery { mediaRepository.uploadImage(validFile, "avatars") } returns AppResult.Success(expected)

        val result = uploadImageUseCase(validFile, "avatars")
        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
        coVerify(exactly = 1) { mediaRepository.uploadImage(validFile, "avatars") }
    }

    // === UploadImageUseCase (ByteArray) ===

    @Test
    fun `uploadImage with empty byte array returns validation error`() = runTest {
        val result = uploadImageUseCase(byteArrayOf(), "test.png", "image/png")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("file", (error as AppError.Validation).details)
    }

    @Test
    fun `uploadImage with valid byte array calls mediaRepository`() = runTest {
        val bytes = "valid-image-bytes".toByteArray()
        val expected = UploadedMedia("https://cloudinary.com/test.png")
        coEvery {
            mediaRepository.uploadImage(bytes, "photo.png", "image/png", "products")
        } returns AppResult.Success(expected)

        val result = uploadImageUseCase(bytes, "photo.png", "image/png", "products")
        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
        coVerify(exactly = 1) { mediaRepository.uploadImage(bytes, "photo.png", "image/png", "products") }
    }

    // === LookupIdentityDocumentUseCase ===

    @Test
    fun `lookupIdentityDocument with invalid DNI length returns validation error`() = runTest {
        val result = lookupIdentityDocumentUseCase(IdentityDocumentType.DNI, "1234")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("number", (error as AppError.Validation).details)
    }

    @Test
    fun `lookupIdentityDocument with non-numeric DNI returns validation error`() = runTest {
        val result = lookupIdentityDocumentUseCase(IdentityDocumentType.DNI, "1234567A")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("number", (error as AppError.Validation).details)
    }

    @Test
    fun `lookupIdentityDocument with valid DNI calls repository`() = runTest {
        val expected = IdentityDocumentData.DniData(
            documentNumber = "72345678",
            firstName = "Juan",
            lastName = "Perez",
            fullName = "Juan Perez",
            verified = true,
            source = "RENIEC"
        )
        coEvery {
            identityValidationRepository.lookupDocument(IdentityDocumentType.DNI, "72345678")
        } returns AppResult.Success(expected)

        val result = lookupIdentityDocumentUseCase(IdentityDocumentType.DNI, "72345678")
        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
        coVerify(exactly = 1) { identityValidationRepository.lookupDocument(IdentityDocumentType.DNI, "72345678") }
    }

    @Test
    fun `lookupIdentityDocument with invalid RUC length returns validation error`() = runTest {
        val result = lookupIdentityDocumentUseCase(IdentityDocumentType.RUC, "2060123456")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("number", (error as AppError.Validation).details)
    }

    @Test
    fun `lookupIdentityDocument with valid RUC calls repository`() = runTest {
        val expected = IdentityDocumentData.RucData(
            documentNumber = "20601234567",
            businessName = "TuxLogic Motors SAC",
            verified = true,
            source = "SUNAT"
        )
        coEvery {
            identityValidationRepository.lookupDocument(IdentityDocumentType.RUC, "20601234567")
        } returns AppResult.Success(expected)

        val result = lookupIdentityDocumentUseCase(IdentityDocumentType.RUC, "20601234567")
        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
        coVerify(exactly = 1) { identityValidationRepository.lookupDocument(IdentityDocumentType.RUC, "20601234567") }
    }

    @Test
    fun `lookupIdentityDocument with invalid CE length returns validation error`() = runTest {
        val result = lookupIdentityDocumentUseCase(IdentityDocumentType.CE, "123")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("number", (error as AppError.Validation).details)
    }

    @Test
    fun `lookupIdentityDocument with valid CE calls repository`() = runTest {
        val expected = IdentityDocumentData.CeData(
            documentNumber = "001234567",
            firstName = "Carlos",
            lastName = "Morales",
            fullName = "Carlos Morales",
            nationality = "VENEZOLANA",
            verified = true,
            source = "MIGRACIONES"
        )
        coEvery {
            identityValidationRepository.lookupDocument(IdentityDocumentType.CE, "001234567")
        } returns AppResult.Success(expected)

        val result = lookupIdentityDocumentUseCase(IdentityDocumentType.CE, "001234567")
        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
        coVerify(exactly = 1) { identityValidationRepository.lookupDocument(IdentityDocumentType.CE, "001234567") }
    }

    // === CheckHealthUseCase ===

    @Test
    fun `checkHealth invoke calls healthRepository getHealth`() = runTest {
        val health = SystemHealth(status = "UP")
        coEvery { healthRepository.getHealth() } returns AppResult.Success(health)

        val result = checkHealthUseCase()
        assertTrue(result is AppResult.Success)
        assertEquals(health, (result as AppResult.Success).data)
        coVerify(exactly = 1) { healthRepository.getHealth() }
    }

    @Test
    fun `checkHealth checkPing calls healthRepository getRootPing`() = runTest {
        val health = SystemHealth(status = "UP", app = "shiftiq-backend", docs = "/api/v1/swagger-ui.html")
        coEvery { healthRepository.getRootPing() } returns AppResult.Success(health)

        val result = checkHealthUseCase.checkPing()
        assertTrue(result is AppResult.Success)
        assertEquals(health, (result as AppResult.Success).data)
        coVerify(exactly = 1) { healthRepository.getRootPing() }
    }
}
