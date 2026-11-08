package com.tuxlogic.shiftiq.mobile.feature.iot.data.di

import com.tuxlogic.shiftiq.mobile.feature.iot.data.remote.api.IotApiService
import com.tuxlogic.shiftiq.mobile.feature.iot.data.repository.IotRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IotDataModule {

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
