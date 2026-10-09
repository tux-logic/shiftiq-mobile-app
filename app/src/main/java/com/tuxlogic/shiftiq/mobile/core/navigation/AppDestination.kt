package com.tuxlogic.shiftiq.mobile.core.navigation

/**
 * Destinos y rutas de navegación base de la aplicación.
 * Permite que los módulos de feature naveguen sin acoplarse directamente entre sí.
 */
sealed class AppDestination(val route: String) {
    // Flujo de autenticación (Feature IAM)
    data object Login : AppDestination("auth/login")
    data object Register : AppDestination("auth/register")
    data object ForgotPassword : AppDestination("auth/forgot-password")

    // Flujos principales por rol y dashboard general
    data object Dashboard : AppDestination("dashboard")
    data object OwnerDashboard : AppDestination("dashboard/owner")
    data object ManagerDashboard : AppDestination("dashboard/manager")
    data object TechnicianDashboard : AppDestination("dashboard/technician")
    data object CustomerDashboard : AppDestination("dashboard/customer")

    // Feature Core: Talleres, sedes y perfil de dueño
    data object WorkshopList : AppDestination("core/workshops")
    data object CreateWorkshop : AppDestination("core/workshops/create/{ownerId}") {
        fun createRoute(ownerId: String) = "core/workshops/create/$ownerId"
    }
    data object BranchManagement : AppDestination("core/workshops/{workshopId}/branches") {
        fun createRoute(workshopId: String) = "core/workshops/$workshopId/branches"
    }
    data object CreateBranch : AppDestination("core/workshops/{workshopId}/branches/create") {
        fun createRoute(workshopId: String) = "core/workshops/$workshopId/branches/create"
    }
    data object OwnerProfile : AppDestination("core/profile/owner")

    // Rutas operativas
    data object BranchSelection : AppDestination("branches/select")
    data object BranchSelector : AppDestination("branches/select")
    data object WorkOrders : AppDestination("operations/work-orders")
    data object Appointments : AppDestination("fleet/appointments")
    data object Inventory : AppDestination("inventory/products")
    data object Telemetry : AppDestination("iot/telemetry")
}
