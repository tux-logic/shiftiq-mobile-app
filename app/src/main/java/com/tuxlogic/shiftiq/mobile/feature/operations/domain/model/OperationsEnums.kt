package com.tuxlogic.shiftiq.mobile.feature.operations.domain.model

enum class WorkOrderStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    PAID,
    DELETED,
    CANCELLED;

    companion object {
        fun fromString(value: String?): WorkOrderStatus = when (value?.uppercase()?.trim()) {
            "IN_PROGRESS" -> IN_PROGRESS
            "COMPLETED" -> COMPLETED
            "PAID" -> PAID
            "DELETED" -> DELETED
            "CANCELLED", "CANCELED" -> CANCELLED
            else -> PENDING
        }
    }
}

val WorkOrderStatus.isCompleted: Boolean
    get() = this == WorkOrderStatus.COMPLETED || this == WorkOrderStatus.PAID

val WorkOrderStatus.isPaid: Boolean
    get() = this == WorkOrderStatus.PAID

val WorkOrderStatus.isCancelled: Boolean
    get() = this == WorkOrderStatus.CANCELLED || this == WorkOrderStatus.DELETED

enum class TaskStatus {
    PENDING,
    DOING,
    COMPLETED,
    CANCELLED;

    companion object {
        fun fromString(value: String?): TaskStatus = when (value?.uppercase()?.trim()) {
            "DOING", "IN_PROGRESS" -> DOING
            "COMPLETED" -> COMPLETED
            "CANCELLED", "CANCELED" -> CANCELLED
            else -> PENDING
        }
    }
}
