package com.tuxlogic.shiftiq.mobile.features.inventory.infrastructure.di
import com.tuxlogic.shiftiq.mobile.features.inventory.domain.InventoryRepository
import com.tuxlogic.shiftiq.mobile.features.inventory.infrastructure.repositories.InventoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class InventoryRepositoryModule {
 @Binds @Singleton abstract fun bindInventoryRepository(impl:InventoryRepositoryImpl):InventoryRepository
}