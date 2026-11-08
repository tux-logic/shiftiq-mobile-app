package com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.api

import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.dto.MediaUploadResourceDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MediaApiService {

    @Multipart
    @POST("api/v1/media/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("folder") folder: RequestBody? = null
    ): Response<MediaUploadResourceDto>
}
