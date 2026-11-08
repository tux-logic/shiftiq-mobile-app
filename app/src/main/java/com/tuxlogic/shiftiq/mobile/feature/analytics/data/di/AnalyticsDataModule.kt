package com.tuxlogic.shiftiq.mobile.feature.analytics.data.di

import com.tuxlogic.shiftiq.mobile.feature.analytics.data.remote.api.AnalyticsApiService
import com.tuxlogic.shiftiq.mobile.feature.analytics.data.repository.AnalyticsRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.repository.AnalyticsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsDataModule {

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
