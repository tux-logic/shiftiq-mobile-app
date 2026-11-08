package com.tuxlogic.shiftiq.mobile.core.common.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe, non-blocking in-memory event bus implementation backed by Kotlin Coroutines [MutableSharedFlow].
 *
 * Configured with replay = 0 to prevent stale replay upon new subscriptions,
 * extraBufferCapacity = 64 to absorb emission bursts, and [BufferOverflow.DROP_OLDEST]
 * to ensure publishers are never blocked by slow subscribers.
 */
@Singleton
class DomainEventBusImpl @Inject constructor() : DomainEventBus {

    private val _events = MutableSharedFlow<DomainEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val events: SharedFlow<DomainEvent> = _events.asSharedFlow()

    override suspend fun emit(event: DomainEvent) {
        _events.emit(event)
    }

    override fun tryEmit(event: DomainEvent): Boolean {
        return _events.tryEmit(event)
    }
}
