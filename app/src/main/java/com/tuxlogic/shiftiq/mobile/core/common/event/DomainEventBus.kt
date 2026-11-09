package com.tuxlogic.shiftiq.mobile.core.common.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance

/**
 * Marker interface for in-memory domain events in the mobile application.
 * Each domain event represents a significant business state transition that occurred.
 */
interface DomainEvent

/**
 * In-memory reactive event bus contract for publishing and observing DomainEvents
 * across decoupled Bounded Contexts.
 */
interface DomainEventBus {

    /**
     * Cold-to-hot shared flow emitting domain events to all active subscribers.
     */
    val events: SharedFlow<DomainEvent>

    /**
     * Publishes a domain event asynchronously without blocking caller threads.
     */
    suspend fun emit(event: DomainEvent)

    /**
     * Non-suspending attempt to emit a domain event directly if buffer capacity permits.
     * Returns true if emission was successful, false if the event could not be emitted.
     */
    fun tryEmit(event: DomainEvent): Boolean
}

/**
 * Type-safe extension to subscribe strictly to events of type [T].
 */
inline fun <reified T : DomainEvent> DomainEventBus.subscribe(): Flow<T> {
    return events.filterIsInstance<T>()
}
