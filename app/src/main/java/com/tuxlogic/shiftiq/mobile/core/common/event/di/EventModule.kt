package com.tuxlogic.shiftiq.mobile.core.common.event.di

import com.tuxlogic.shiftiq.mobile.core.common.event.DomainEventBus
import com.tuxlogic.shiftiq.mobile.core.common.event.DomainEventBusImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EventModule {

    @Binds
    @Singleton
    abstract fun bindDomainEventBus(
        impl: DomainEventBusImpl
    ): DomainEventBus
}
