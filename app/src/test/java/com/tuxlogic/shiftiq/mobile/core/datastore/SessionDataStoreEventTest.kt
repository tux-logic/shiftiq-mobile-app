package com.tuxlogic.shiftiq.mobile.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.tuxlogic.shiftiq.mobile.core.common.event.ActiveBranchChangedEvent
import com.tuxlogic.shiftiq.mobile.core.common.event.DomainEventBus
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

class SessionDataStoreEventTest {

    private val dataStore: DataStore<Preferences> = mockk()
    private val domainEventBus: DomainEventBus = mockk(relaxed = true)

    private lateinit var sessionDataStore: SessionDataStoreImpl

    private val branchUuid = UUID.randomUUID()
    private val branchId = BranchId(branchUuid)

    @Before
    fun setUp() {
        every { dataStore.data } returns flowOf(emptyPreferences())
        sessionDataStore = SessionDataStoreImpl(dataStore, domainEventBus)
    }

    @Test
    fun `setActiveBranchId emits ActiveBranchChangedEvent when branch changes`() = runTest {
        val transformSlot = slot<suspend (Preferences) -> Preferences>()
        coEvery { dataStore.updateData(capture(transformSlot)) } coAnswers {
            val transform = transformSlot.captured
            transform.invoke(emptyPreferences())
        }

        sessionDataStore.setActiveBranchId(branchId)

        coVerify(exactly = 1) {
            domainEventBus.emit(match { event ->
                event is ActiveBranchChangedEvent && event.newBranchId == branchId && event.previousBranchId == null
            })
        }
    }
}
