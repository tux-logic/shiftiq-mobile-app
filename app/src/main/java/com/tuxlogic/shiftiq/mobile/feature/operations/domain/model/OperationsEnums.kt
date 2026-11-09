package com.tuxlogic.shiftiq.mobile.feature.operations.domain.model

enum class WorkOrderStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    companion object {
        fun fromString(value: String?): WorkOrderStatus = when (value?.uppercase()?.trim()) {
            "IN_PROGRESS" -> IN_PROGRESS
            "COMPLETED" -> COMPLETED
            "CANCELLED", "CANCELED" -> CANCELLED
            else -> PENDING
        }
    }
}

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
