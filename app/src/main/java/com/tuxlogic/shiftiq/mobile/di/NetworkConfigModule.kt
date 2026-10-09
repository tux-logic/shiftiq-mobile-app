package com.tuxlogic.shiftiq.mobile.di

import com.tuxlogic.shiftiq.mobile.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkConfigModule {

    @Provides
    @Singleton
    @Named("BaseUrl")
    fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }
}
