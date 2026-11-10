package com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.UploadedMedia
import java.io.File

interface MediaRepository {
    suspend fun uploadImage(file: File, folder: String? = null): AppResult<UploadedMedia>
    suspend fun uploadImage(fileBytes: ByteArray, fileName: String, mimeType: String, folder: String? = null): AppResult<UploadedMedia>
}
