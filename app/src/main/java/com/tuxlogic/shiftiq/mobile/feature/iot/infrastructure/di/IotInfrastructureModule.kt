package com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.di

import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.api.IotApiService
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.repository.IotRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IotInfrastructureModule {

    @Binds
    @Singleton
    abstract fun bindIotRepository(
        impl: IotRepositoryImpl
    ): IotRepository

    companion object {
        @Provides
        @Singleton
        fun provideIotApiService(
            retrofit: Retrofit
        ): IotApiService {
            return retrofit.create(IotApiService::class.java)
        }
    }
}
