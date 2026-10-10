package com.tuxlogic.shiftiq.mobile.feature.fleet.data.di

import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.api.FleetApiService
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.repository.FleetRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.repository.FleetRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FleetDataModule {

    @Binds
    @Singleton
    abstract fun bindFleetRepository(
        impl: FleetRepositoryImpl
    ): FleetRepository

    companion object {
        @Provides
        @Singleton
        fun provideFleetApiService(
            retrofit: Retrofit
        ): FleetApiService {
            return retrofit.create(FleetApiService::class.java)
        }
    }
}
