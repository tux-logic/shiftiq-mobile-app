package com.tuxlogic.shiftiq.mobile.core.model

/**
 * Roles del sistema, alineados con el enum `Roles.java` de ShiftIQ Platform.
 */
enum class Role(val rank: Int) {
    ROLE_USER(0),
    ROLE_EMPLOYEE(0),
    ROLE_ASSISTANT(1),
    ROLE_BRANCH_MANAGER(2),
    ROLE_OWNER(3),
    ROLE_ADMIN(4);

    /**
     * Comprueba si este rol tiene al menos el mismo nivel jerárquico que [other].
     */
    fun hasAtLeast(other: Role): Boolean = this.rank >= other.rank

    /**
     * Comprueba si el rol puede gestionar personal técnico u operativo.
     */
    val canManageStaff: Boolean
        get() = this == ROLE_ADMIN || this == ROLE_OWNER || this == ROLE_BRANCH_MANAGER || this == ROLE_ASSISTANT

    /**
     * Nombre amigable para mostrar en UI.
     */
    val displayName: String
        get() = when (this) {
            ROLE_ADMIN -> "Administrador"
            ROLE_OWNER -> "Dueño de Taller"
            ROLE_BRANCH_MANAGER -> "Gerente de Sede"
            ROLE_ASSISTANT -> "Recepción / Asistente"
            ROLE_EMPLOYEE -> "Técnico / Mecánico"
            ROLE_USER -> "Cliente / Usuario"
        }

    companion object {
        fun fromName(name: String?): Role? {
            if (name.isNullOrBlank()) return null
            val normalized = name.trim().uppercase().let {
                if (!it.startsWith("ROLE_")) "ROLE_$it" else it
            }
            return entries.firstOrNull { it.name == normalized }
        }
    }
}
