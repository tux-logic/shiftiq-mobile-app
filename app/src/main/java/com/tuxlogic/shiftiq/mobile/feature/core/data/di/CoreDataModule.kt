package com.tuxlogic.shiftiq.mobile.feature.core.data.di

import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.api.CoreApiService
import com.tuxlogic.shiftiq.mobile.feature.core.data.repository.CoreRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreDataModule {

    @Binds
    @Singleton
    abstract fun bindCoreRepository(
        impl: CoreRepositoryImpl
    ): CoreRepository

    companion object {
        @Provides
        @Singleton
        fun provideCoreApiService(
            retrofit: Retrofit
        ): CoreApiService {
            return retrofit.create(CoreApiService::class.java)
        }
    }
}
