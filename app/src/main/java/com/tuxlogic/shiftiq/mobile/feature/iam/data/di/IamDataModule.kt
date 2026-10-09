package com.tuxlogic.shiftiq.mobile.feature.iam.data.di

import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.api.AuthApiService
import com.tuxlogic.shiftiq.mobile.feature.iam.data.repository.AuthRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IamBindingModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}

@Module
@InstallIn(SingletonComponent::class)
object IamNetworkModule {

    @Provides
    @Singleton
    fun provideAuthApiService(
        retrofit: Retrofit
    ): AuthApiService = retrofit.create(AuthApiService::class.java)
}
