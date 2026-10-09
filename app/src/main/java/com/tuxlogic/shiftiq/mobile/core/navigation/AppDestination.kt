package com.tuxlogic.shiftiq.mobile.core.navigation

/**
 * Destinos y rutas de navegación base de la aplicación.
 * Permite que los módulos de feature naveguen sin acoplarse directamente entre sí.
 */
sealed class AppDestination(val route: String) {
    // Flujo de autenticación (Feature IAM)
    data object Login : AppDestination("auth/login")
    data object ForgotPassword : AppDestination("auth/forgot-password")

    // Flujos principales por rol y dashboard general
    data object Dashboard : AppDestination("dashboard")
    data object OwnerDashboard : AppDestination("dashboard/owner")
    data object ManagerDashboard : AppDestination("dashboard/manager")
    data object TechnicianDashboard : AppDestination("dashboard/technician")
    data object CustomerDashboard : AppDestination("dashboard/customer")

    // Rutas operativas
    data object BranchSelection : AppDestination("branches/select")
    data object BranchSelector : AppDestination("branches/select")
    data object WorkOrders : AppDestination("operations/work-orders")
    data object Appointments : AppDestination("fleet/appointments")
    data object Inventory : AppDestination("inventory/products")
    data object Telemetry : AppDestination("iot/telemetry")
}
