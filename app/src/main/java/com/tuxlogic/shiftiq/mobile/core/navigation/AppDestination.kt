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
    // Feature Fleet: Citas y gestión de personal
    data object Appointments : AppDestination("fleet/appointments")
    data object CreateAppointment : AppDestination("fleet/appointments/create")
    data object StaffManagement : AppDestination("fleet/staff")
    data object Inventory : AppDestination("inventory/products")
    data object Telemetry : AppDestination("iot/telemetry")
    data object CustomerVehicles : AppDestination("iot/customer-vehicles")

    // Flujo B2C Conductor (Fleet & Operations Bounded Context)
    data object B2CMain : AppDestination("b2c/main")
    data object B2CHome : AppDestination("b2c/home")
    data object B2CDiagnostics : AppDestination("b2c/diagnostics")
    data object B2CWorkshops : AppDestination("b2c/workshops")
    data object B2CGarage : AppDestination("b2c/garage")
    data object B2CVehicleRegistration : AppDestination("b2c/garage/register-vehicle")
    data object B2CDtcDetail : AppDestination("b2c/diagnostics/dtc/{dtcCode}") {
        fun createRoute(dtcCode: String) = "b2c/diagnostics/dtc/$dtcCode"
    }
    data object B2COfflineTelemetry : AppDestination("b2c/diagnostics/offline")
    data object B2CAppointmentBooking : AppDestination("b2c/workshops/{workshopId}/booking") {
        fun createRoute(workshopId: String) = "b2c/workshops/$workshopId/booking"
    }
    data object B2CLiveWorkOrder : AppDestination("b2c/work-order/{workOrderId}") {
        fun createRoute(workOrderId: String) = "b2c/work-order/$workOrderId"
    }
}
