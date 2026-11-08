package com.tuxlogic.shiftiq.mobile.features.inventory.infrastructure.di
import com.tuxlogic.shiftiq.mobile.features.inventory.infrastructure.remote.InventoryApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object InventoryApiModule {
 @Provides @Singleton fun provideInventoryApiService(retrofit:Retrofit):InventoryApiService=retrofit.create(InventoryApiService::class.java)
}