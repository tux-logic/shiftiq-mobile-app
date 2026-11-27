package com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.di

import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.api.CoreApiService
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.repository.CoreRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.OperatingHoursRepository
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.api.OperatingHoursApiService
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.repository.OperatingHoursRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreInfrastructureModule {

    @Binds
    @Singleton
    abstract fun bindCoreRepository(
        impl: CoreRepositoryImpl
    ): CoreRepository

    @Binds
    @Singleton
    abstract fun bindOperatingHoursRepository(
        impl: OperatingHoursRepositoryImpl
    ): OperatingHoursRepository

    companion object {
        @Provides
        @Singleton
        fun provideCoreApiService(
            retrofit: Retrofit
        ): CoreApiService {
            return retrofit.create(CoreApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideOperatingHoursApiService(retrofit: Retrofit): OperatingHoursApiService =
            retrofit.create(OperatingHoursApiService::class.java)
    }
}
