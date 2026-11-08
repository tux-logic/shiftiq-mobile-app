package com.tuxlogic.shiftiq.mobile.feature.shared.data.di

import com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.api.HealthApiService
import com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.api.IdentityValidationApiService
import com.tuxlogic.shiftiq.mobile.feature.shared.data.remote.api.MediaApiService
import com.tuxlogic.shiftiq.mobile.feature.shared.data.repository.HealthRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.shared.data.repository.IdentityValidationRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.shared.data.repository.MediaRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.HealthRepository
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.IdentityValidationRepository
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SharedDataModule {

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        impl: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindIdentityValidationRepository(
        impl: IdentityValidationRepositoryImpl
    ): IdentityValidationRepository

    @Binds
    @Singleton
    abstract fun bindHealthRepository(
        impl: HealthRepositoryImpl
    ): HealthRepository

    companion object {
        @Provides
        @Singleton
        fun provideMediaApiService(
            retrofit: Retrofit
        ): MediaApiService {
            return retrofit.create(MediaApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideIdentityValidationApiService(
            retrofit: Retrofit
        ): IdentityValidationApiService {
            return retrofit.create(IdentityValidationApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideHealthApiService(
            retrofit: Retrofit
        ): HealthApiService {
            return retrofit.create(HealthApiService::class.java)
        }
    }
}
