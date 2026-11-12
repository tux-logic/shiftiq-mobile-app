package com.tuxlogic.shiftiq.mobile.feature.shared.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.UploadedMedia
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.MediaRepository
import java.io.File
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    companion object {
        const val MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024 // 10MB
        val SUPPORTED_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp", "gif", "svg", "avif")
    }

    suspend operator fun invoke(file: File, folder: String? = "uploads"): AppResult<UploadedMedia> {
        if (!file.exists() || file.length() == 0L) {
            return AppResult.Failure(
                AppError.Validation(details = "file", message = "File does not exist or is empty")
            )
        }
        if (file.length() > MAX_IMAGE_SIZE_BYTES) {
            return AppResult.Failure(
                AppError.Validation(details = "file", message = "File size exceeds 10MB limit")
            )
        }
        val extension = file.extension.lowercase()
        if (extension !in SUPPORTED_EXTENSIONS) {
            return AppResult.Failure(
                AppError.Validation(details = "file", message = "Unsupported image format: .$extension")
            )
        }
        return mediaRepository.uploadImage(file, folder)
    }

    suspend operator fun invoke(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        folder: String? = "uploads"
    ): AppResult<UploadedMedia> {
        if (fileBytes.isEmpty()) {
            return AppResult.Failure(
                AppError.Validation(details = "file", message = "File binary is empty")
            )
        }
        if (fileBytes.size > MAX_IMAGE_SIZE_BYTES) {
            return AppResult.Failure(
                AppError.Validation(details = "file", message = "File size exceeds 10MB limit")
            )
        }
        return mediaRepository.uploadImage(fileBytes, fileName, mimeType, folder)
    }
}
