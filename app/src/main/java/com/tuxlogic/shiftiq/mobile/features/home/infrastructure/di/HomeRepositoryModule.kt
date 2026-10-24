package com.tuxlogic.shiftiq.mobile.features.home.infrastructure.di

import com.tuxlogic.shiftiq.mobile.features.home.domain.HomeRepository
import com.tuxlogic.shiftiq.mobile.features.home.infrastructure.repositories.HomeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository
}
