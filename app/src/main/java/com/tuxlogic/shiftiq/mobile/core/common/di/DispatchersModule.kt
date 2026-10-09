package com.tuxlogic.shiftiq.mobile.core.common.di

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DefaultDispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DispatchersModule {

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(
        impl: DefaultDispatcherProvider
    ): DispatcherProvider
}
