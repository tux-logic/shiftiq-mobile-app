package com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.di

import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.repository.AnalyticsRepository
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.api.AnalyticsApiService
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.repository.AnalyticsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsInfrastructureModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        impl: AnalyticsRepositoryImpl
    ): AnalyticsRepository

    companion object {
        @Provides
        @Singleton
        fun provideAnalyticsApiService(
            retrofit: Retrofit
        ): AnalyticsApiService {
            return retrofit.create(AnalyticsApiService::class.java)
        }
    }
}
