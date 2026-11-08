package com.tuxlogic.shiftiq.mobile.feature.inventory.data.di

import com.tuxlogic.shiftiq.mobile.feature.inventory.data.remote.api.InventoryApiService
import com.tuxlogic.shiftiq.mobile.feature.inventory.data.repository.InventoryRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.repository.InventoryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InventoryDataModule {

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        impl: InventoryRepositoryImpl
    ): InventoryRepository

    companion object {
        @Provides
        @Singleton
        fun provideInventoryApiService(
            retrofit: Retrofit
        ): InventoryApiService {
            return retrofit.create(InventoryApiService::class.java)
        }
    }
}
