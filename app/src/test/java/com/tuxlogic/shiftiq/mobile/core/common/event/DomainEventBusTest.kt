package com.tuxlogic.shiftiq.mobile.core.common.event

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private data class TestEventA(val message: String) : DomainEvent
private data class TestEventB(val code: Int) : DomainEvent
private data object TestEventC : DomainEvent

@OptIn(ExperimentalCoroutinesApi::class)
class DomainEventBusTest {

    private lateinit var eventBus: DomainEventBusImpl

    @Before
    fun setUp() {
        eventBus = DomainEventBusImpl()
    }

    @Test
    fun `emit delivers event to active subscriber`() = runTest {
        val received = mutableListOf<DomainEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            eventBus.events.take(1).toList(received)
        }

        eventBus.emit(TestEventA("hello"))
        job.join()

        assertEquals(1, received.size)
        assertEquals(TestEventA("hello"), received[0])
    }

    @Test
    fun `multiple subscribers all receive emitted event`() = runTest {
        val subscriber1Events = mutableListOf<DomainEvent>()
        val subscriber2Events = mutableListOf<DomainEvent>()

        val job1 = launch(UnconfinedTestDispatcher(testScheduler)) { eventBus.events.take(1).toList(subscriber1Events) }
        val job2 = launch(UnconfinedTestDispatcher(testScheduler)) { eventBus.events.take(1).toList(subscriber2Events) }

        eventBus.emit(TestEventB(42))
        job1.join()
        job2.join()

        assertEquals(listOf<DomainEvent>(TestEventB(42)), subscriber1Events)
        assertEquals(listOf<DomainEvent>(TestEventB(42)), subscriber2Events)
    }

    @Test
    fun `subscribe with reified type filter receives only matching events`() = runTest {
        val filteredEvents = mutableListOf<TestEventA>()

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            eventBus.subscribe<TestEventA>().take(2).toList(filteredEvents)
        }

        eventBus.emit(TestEventA("first"))
        eventBus.emit(TestEventB(99))
        eventBus.emit(TestEventC)
        eventBus.emit(TestEventA("second"))

        job.join()

        assertEquals(2, filteredEvents.size)
        assertEquals("first", filteredEvents[0].message)
        assertEquals("second", filteredEvents[1].message)
    }

    @Test
    fun `tryEmit delivers event without suspending when buffer allows`() = runTest {
        val received = mutableListOf<DomainEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            eventBus.events.take(1).toList(received)
        }

        val success = eventBus.tryEmit(TestEventA("non-suspending"))
        job.join()

        assertTrue(success)
        assertEquals(1, received.size)
        assertEquals(TestEventA("non-suspending"), received[0])
    }

    @Test
    fun `buffer overflow drops oldest events without blocking emitter`() = runTest {
        for (i in 1..80) {
            val emitted = eventBus.tryEmit(TestEventB(i))
            assertTrue(emitted)
        }

        val received = mutableListOf<DomainEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            eventBus.events.take(1).toList(received)
        }

        eventBus.emit(TestEventB(100))
        job.join()

        assertEquals(1, received.size)
        assertEquals(TestEventB(100), received[0])
    }
}
