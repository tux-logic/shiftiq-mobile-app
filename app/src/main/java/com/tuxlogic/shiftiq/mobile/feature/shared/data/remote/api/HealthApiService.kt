package com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.api

import com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.dto.HealthResourceDto
import com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.dto.RootPingResourceDto
import retrofit2.Response
import retrofit2.http.GET

interface HealthApiService {

    @GET("/")
    suspend fun getRootPing(): Response<RootPingResourceDto>

    @GET("health")
    suspend fun getHealth(): Response<HealthResourceDto>
}
