package com.tuxlogic.shiftiq.mobile.feature.operations.data.di

import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.api.ServicesApiService
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.api.WorkOrdersApiService
import com.tuxlogic.shiftiq.mobile.feature.operations.data.repository.ServicesRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.operations.data.repository.WorkOrderRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.ServicesRepository
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OperationsDataModule {

    @Binds
    @Singleton
    abstract fun bindServicesRepository(
        impl: ServicesRepositoryImpl
    ): ServicesRepository

    @Binds
    @Singleton
    abstract fun bindWorkOrderRepository(
        impl: WorkOrderRepositoryImpl
    ): WorkOrderRepository

    companion object {
        @Provides
        @Singleton
        fun provideServicesApiService(
            retrofit: Retrofit
        ): ServicesApiService {
            return retrofit.create(ServicesApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideWorkOrdersApiService(
            retrofit: Retrofit
        ): WorkOrdersApiService {
            return retrofit.create(WorkOrdersApiService::class.java)
        }
    }
}
