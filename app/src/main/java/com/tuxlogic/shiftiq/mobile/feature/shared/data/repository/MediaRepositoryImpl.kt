package com.tuxlogic.shiftiq.mobile.feature.shared.data.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.shared.data.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.api.MediaApiService
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.UploadedMedia
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.MediaRepository
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val mediaApiService: MediaApiService,
    private val dispatchers: DispatcherProvider
) : MediaRepository {

    override suspend fun uploadImage(file: File, folder: String?): AppResult<UploadedMedia> =
        withContext(dispatchers.io) {
            val mimeType = when (file.extension.lowercase()) {
                "png" -> "image/png"
                "webp" -> "image/webp"
                "gif" -> "image/gif"
                "svg" -> "image/svg+xml"
                "avif" -> "image/avif"
                else -> "image/jpeg"
            }
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val folderBody = folder?.toRequestBody("text/plain".toMediaTypeOrNull())

            safeApiCall { mediaApiService.uploadImage(part, folderBody) }
                .map { it.toDomain() }
        }

    override suspend fun uploadImage(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        folder: String?
    ): AppResult<UploadedMedia> = withContext(dispatchers.io) {
        val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", fileName, requestBody)
        val folderBody = folder?.toRequestBody("text/plain".toMediaTypeOrNull())

        safeApiCall { mediaApiService.uploadImage(part, folderBody) }
            .map { it.toDomain() }
    }
}
