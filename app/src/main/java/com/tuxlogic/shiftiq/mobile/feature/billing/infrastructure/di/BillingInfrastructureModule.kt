package com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.di

import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.api.BillingApiService
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.repository.BillingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BillingInfrastructureModule {

    @Binds
    @Singleton
    abstract fun bindBillingRepository(
        impl: BillingRepositoryImpl
    ): BillingRepository

    companion object {
        @Provides
        @Singleton
        fun provideBillingApiService(
            retrofit: Retrofit
        ): BillingApiService {
            return retrofit.create(BillingApiService::class.java)
        }
    }
}
