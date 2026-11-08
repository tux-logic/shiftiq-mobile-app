package com.tuxlogic.shiftiq.mobile.core.common.event

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.UserId

/**
 * Emitted when the currently selected active workshop branch changes.
 * Consumers (operations, fleet, inventory, billing, analytics) observe this
 * to reload branch-scoped data or invalidate local caches.
 */
data class ActiveBranchChangedEvent(
    val previousBranchId: BranchId?,
    val newBranchId: BranchId,
    val timestamp: Long = System.currentTimeMillis()
) : DomainEvent

/**
 * Emitted when authentication credentials expire or the refresh token is rejected (HTTP 401).
 * Informs all feature modules to cancel ongoing network operations and purge session state.
 */
data class SessionExpiredEvent(
    val reason: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : DomainEvent

/**
 * Emitted when the user explicitly triggers logout from the application.
 */
data class UserLoggedOutEvent(
    val userId: UserId? = null,
    val timestamp: Long = System.currentTimeMillis()
) : DomainEvent
