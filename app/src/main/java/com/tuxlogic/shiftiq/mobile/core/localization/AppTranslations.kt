package com.tuxlogic.shiftiq.mobile.core.localization

import android.content.Context
import org.json.JSONObject

object AppTranslations {
    private val spanishToEnglish = linkedMapOf<String, String>()
    private val englishToSpanish = linkedMapOf<String, String>()
    private var initialized = false

    private val manualPairs = listOf(
        "Gestión Inteligente de Talleres Automotrices" to "Smart Automotive Workshop Management",
        "Bienvenido de vuelta" to "Welcome back",
        "Inicia sesión para continuar con tu portal ShiftIQ" to "Sign in to continue to your ShiftIQ portal",
        "¿No tienes una cuenta?" to "Don't have an account?",
        "¿Ya tienes una cuenta?" to "Already have an account?",
        " Crear cuenta" to " Create account",
        " Crear una cuenta" to " Create an account",
        "Crear cuenta" to "Create account",
        "Iniciar sesión" to "Sign in",
        "Iniciar Sesión" to "Sign In",
        "Inicia sesión" to "Sign in",
        "Correo electrónico" to "Email address",
        "Correo Electrónico" to "Email Address",
        "Contraseña" to "Password",
        "Tu contraseña" to "Your password",
        "¿Olvidaste tu contraseña?" to "Forgot your password?",
        "Recuperar Contraseña" to "Recover Password",
        "Mostrar contraseña" to "Show password",
        "Ocultar contraseña" to "Hide password",
        "Ingresa un correo electrónico válido" to "Enter a valid email address",
        "La contraseña debe tener al menos 8 caracteres" to "The password must be at least 8 characters",
        "Clientes · Personal · Talleros" to "Customers · Staff · Workshop owners",
        "Guardar" to "Save",
        "Cancelar" to "Cancel",
        "Confirmar" to "Confirm",
        "Completar" to "Complete",
        "Abrir OT" to "Open work order",
        "Aceptar" to "Accept",
        "Rechazar" to "Reject",
        "Cerrar" to "Close",
        "Cerrar Sesión" to "Sign Out",
        "¿Cerrar Sesión?" to "Sign out?",
        "Seguridad" to "Security",
        "Desbloqueo con huella o rostro" to "Unlock with fingerprint or face",
        "Usa la biometría para iniciar sesión rápidamente" to "Use biometrics to sign in quickly",
        "Desbloqueo biométrico" to "Biometric unlock",
        "Desbloqueo biométrico activado" to "Biometric unlock enabled",
        "Desbloqueo biométrico desactivado" to "Biometric unlock disabled",
        "Confirma tu identidad para activar el desbloqueo rápido" to "Confirm your identity to enable quick unlock",
        "Desbloquea ShiftIQ" to "Unlock ShiftIQ",
        "Confirma tu identidad para continuar" to "Confirm your identity to continue",
        "Usa tu huella o rostro para continuar." to "Use your fingerprint or face to continue.",
        "Verificando…" to "Verifying…",
        "Desbloquear" to "Unlock",
        "Usar otra cuenta" to "Use another account",
        "Volver" to "Back",
        "Atrás" to "Back",
        "Continuar" to "Continue",
        "Buscar" to "Search",
        "Agregar" to "Add",
        "Añadir" to "Add",
        "Eliminar" to "Delete",
        "Editar" to "Edit",
        "Modificar" to "Edit",
        "Ver" to "View",
        "Ver Detalle" to "View Details",
        "Listo" to "Done",
        "Disponible" to "Available",
        "Activa" to "Active",
        "Activos" to "Active",
        "Pendiente" to "Pending",
        "Completada" to "Completed",
        "Completado" to "Completed",
        "Error" to "Error",
        "Cargando..." to "Loading...",
        "Guardando…" to "Saving…",
        "Conectando…" to "Connecting…",
        "Configuración" to "Settings",
        "Principal" to "Primary",
        "Resumen del taller" to "Workshop overview",
        "Panel de propietario" to "Owner dashboard",
        "Actualizar" to "Refresh",
        "Sucursal activa" to "Active branch",
        "Directorio de clientes" to "Customer directory",
        "Clientes registrados en esta sede" to "Customers registered at this branch",
        "Buscar cliente" to "Search customers",
        "clientes registrados" to "registered customers",
        "No se encontraron clientes" to "No customers found",
        "Prueba con otro término o revisa los registros de esta sede." to "Try another search term or check this branch's registrations.",
        "Selecciona una sede activa para ver sus clientes." to "Select an active branch to view its customers.",
        "Nueva cita" to "New appointment",
        "Agendar Nueva Cita" to "Schedule New Appointment",
        "Seleccionar Cliente Registrado (Opcional)" to "Select Registered Customer (Optional)",
        "Cliente Nuevo / Ingresar Manualmente" to "New Customer / Enter Manually",
        "Nombre Completo *" to "Full Name *",
        "Vehículo registrado" to "Registered vehicle",
        "Seleccionar vehículo registrado" to "Select registered vehicle",
        "Bahía Asignada:" to "Assigned Bay:",
        "Elegir fecha" to "Choose date",
        "Elegir hora" to "Choose time",
        "Cargando servicios de la sede..." to "Loading branch services...",
        "No hay servicios creados en esta sede." to "There are no services created at this branch.",
        "Confirmar y Agendar" to "Confirm and Schedule",
        "Ingrese el cliente" to "Enter the customer name",
        "Ingrese el cliente." to "Enter the customer name.",
        "Ingrese placa" to "Enter the license plate",
        "Ingrese la placa del vehículo." to "Enter the vehicle license plate.",
        "Seleccione un servicio." to "Select a service.",
        "Selecciona una sede activa para agendar una cita." to "Select an active branch to schedule an appointment.",
        "La sede activa no tiene un identificador válido." to "The active branch has an invalid identifier.",
        "Facturación" to "Billing",
        "Comprobantes y cotizaciones" to "Vouchers and quotes",
        "Comprobantes" to "Vouchers",
        "Cotizaciones" to "Quotes",
        "Cobrado hoy" to "Collected today",
        "Pagos pendientes" to "Pending payments",
        "No hay comprobantes emitidos." to "No issued vouchers.",
        "No hay cotizaciones registradas." to "No quotes registered.",
        "Desliza hacia abajo para actualizar." to "Pull down to refresh.",
        "Selecciona una sede activa para consultar la facturación." to "Select an active branch to view billing.",
        "Mi perfil" to "My profile",
        "Información personal" to "Personal information",
        "Usuario ShiftIQ" to "ShiftIQ user",
        "Cambiar sede" to "Switch branch",
        "Ingresos de hoy" to "Today's revenue",
        "Ingresos del mes" to "Monthly revenue",
        "Red" to "Network",
        "Órdenes completadas" to "Completed work orders",
        "Citas de hoy" to "Today's appointments",
        "Alertas de stock" to "Low-stock alerts",
        "Alertas IoT" to "IoT alerts",
        "Resumen de red" to "Network overview",
        "Talleres activos" to "Active workshops",
        "Ingresos de red" to "Network revenue",
        "Citas de red" to "Network appointments",
        "Accesos del taller" to "Workshop shortcuts",
        "Administrar talleres y sucursales" to "Manage workshops and branches",
        "Mi resumen" to "My dashboard",
        "Cola operativa" to "Work queue",
        "Tareas pendientes" to "Pending tasks",
        "Completadas" to "Completed",
        "Alertas de diagnóstico" to "Diagnostic alerts",
        "alertas activas" to "active alerts",
        "Mi cola de trabajo" to "My work queue",
        "Ver todas" to "View all",
        "No tienes órdenes asignadas." to "You have no assigned work orders.",
        "Órdenes de trabajo" to "Work orders",
        "Órdenes de la sucursal activa" to "Work orders for the active branch",
        "No hay órdenes asignadas para mostrar." to "There are no assigned work orders to show.",
        "Órdenes" to "Work orders",
        "Citas" to "Appointments",
        "Stock saludable" to "Stock healthy",
        "Reponer stock" to "Restock needed",
        "Revisión OBD2 necesaria" to "OBD2 review needed",
        "Telemetría estable" to "Telemetry stable",
        "Inicio" to "Home",
        "Inventario" to "Inventory",
        "IoT" to "IoT",
        "Horarios y Feriados" to "Operating Hours and Holidays",
        "Catálogo de servicios" to "Services Catalog",
        "Servicios de mano de obra de la sede" to "Branch labor services",
        "Talleres y sedes" to "Workshops and branches",
        "Equipo y personal" to "Team and staff",
        "Selecciona una sede activa para continuar." to "Select an active branch to continue.",
        "Selecciona una sede activa para ver sus servicios." to "Select an active branch to view its services.",
        "Eliminar servicio" to "Delete service",
        "¿Eliminar este servicio de esta sede?" to "Delete this service from this branch?",
        "En progreso" to "In progress",
        "Reintentar" to "Retry",
        "Nueva orden de trabajo" to "New work order",
        "Registra el ingreso desde una cita pendiente" to "Create the check-in from a pending appointment",
        "CITA PENDIENTE" to "PENDING APPOINTMENT",
        "No hay citas pendientes en esta sede. Agenda una cita antes de abrir una orden." to "There are no pending appointments at this branch. Schedule an appointment before opening a work order.",
        "Seleccionar cita" to "Select appointment",
        "Cliente " to "Customer ",
        " · Vehículo " to " · Vehicle ",
        "DATOS DE INGRESO" to "CHECK-IN DETAILS",
        "Kilometraje de entrada" to "Check-in mileage",
        "Diagnóstico inicial" to "Initial diagnosis",
        "Alerta OBD" to "OBD alert",
        "Código DTC" to "DTC code",
        "Recomendación de diagnóstico" to "Diagnostic recommendation",
        "Marcar como resuelta" to "Mark as resolved",
        "Abrir OT en taller" to "Open workshop work order",
        "No se pudo actualizar el estado de la alerta: el backend todavía no permite esta operación." to "The alert status could not be updated: the backend does not support this operation yet.",
        "Describe el diagnóstico inicial." to "Describe the initial diagnosis.",
        "Ingresa el kilometraje de entrada." to "Enter the check-in mileage.",
        "Selecciona una cita pendiente." to "Select a pending appointment.",
        "Abrir orden de trabajo" to "Open work order",
        "No hay órdenes activas en esta sucursal." to "There are no active work orders at this branch.",
        "No hay órdenes para este filtro." to "There are no work orders for this filter.",
        "Nueva OT" to "New work order",
        "Nombre del servicio" to "Service name",
        "Precio (S/)" to "Price (S/)",
        "Ingresa el nombre del servicio." to "Enter the service name.",
        "Ingresa un precio mayor a 0." to "Enter a price greater than 0.",
        "Servicio registrado en la sede." to "Service added to the branch.",
        "Servicio actualizado." to "Service updated.",
        "Servicio eliminado." to "Service deleted.",
        "Nuevo servicio" to "New service",
        "Editar servicio" to "Edit service",
        "No hay servicios activos. Agrega el primer servicio de la sede." to "There are no active services. Add the first service for this branch.",
        "Configura el horario del taller" to "Set the workshop schedule",
        "Horario semanal" to "Weekly schedule",
        "Esta sede" to "This branch",
        "Todo el taller" to "Entire workshop",
        "Guardar horario" to "Save schedule",
        "Usar horario del taller" to "Use workshop schedule",
        "Feriados y excepciones" to "Holidays and exceptions",
        "No hay excepciones registradas." to "No exceptions have been added.",
        "Agregar" to "Add",
        "Nueva excepción" to "New exception",
        "Abrir con horario especial" to "Open with special hours",
        "Motivo" to "Reason",
        "Abierto" to "Open",
        "Cerrado" to "Closed",
        "Apertura" to "Opening",
        "Cierre" to "Closing",
        "Lunes" to "Monday",
        "Martes" to "Tuesday",
        "Miércoles" to "Wednesday",
        "Jueves" to "Thursday",
        "Viernes" to "Friday",
        "Sábado" to "Saturday",
        "Domingo" to "Sunday",
        "Horario guardado." to "Operating hours saved.",
        "Esta sede ahora usa el horario del taller." to "This branch now inherits the workshop schedule.",
        "Horario especial guardado." to "Special hours saved.",
        "Excepción eliminada." to "Special hours removed.",
        "Selecciona primero una sede activa." to "Select an active branch first.",
        "Selecciona una sede activa para consultar la telemetría." to "Select an active branch to view telemetry.",
        "El backend no devolvió el identificador del vehículo para esta alerta." to "The backend did not return the vehicle ID for this alert.",
        "Telemetría IoT" to "IoT telemetry",
        "Alertas y sensores de la sede" to "Branch alerts and sensors",
        "Sensores en vivo" to "Live sensors",
        "Detalle del diagnóstico" to "Diagnostic details",
        "Estado de alertas" to "Alert status",
        "Severidad" to "Severity",
        "Todas" to "All",
        "Activas" to "Active",
        "En revisión" to "In inspection",
        "Resueltas" to "Resolved",
        "Crítica" to "Critical",
        "Alta" to "High",
        "Moderada" to "Moderate",
        "Baja" to "Low",
        "Críticas" to "Critical",
        "Moderadas" to "Moderate",
        "No hay alertas de diagnóstico para esta sede." to "There are no diagnostic alerts for this branch.",
        "No hay alertas con esos filtros." to "There are no alerts with those filters.",
        "Las alertas se consultan desde los dispositivos OBD-II activos." to "Alerts are loaded from active OBD-II devices.",
        "Resuelta" to "Resolved",
        "Sistema del vehículo" to "Vehicle system",
        "Recomendación de diagnóstico" to "Diagnostic recommendation",
        "Datos al detectar la alerta" to "Data captured when the alert was detected",
        "Ver sensores del vehículo" to "View vehicle sensors",
        "Ir a órdenes de trabajo" to "Go to work orders",
        "Lectura registrada más reciente" to "Most recent recorded reading",
        "No hay lecturas de telemetría para este vehículo." to "There are no telemetry readings for this vehicle.",
        "Actualizar lectura" to "Refresh reading",
        "RPM del motor" to "Engine RPM",
        "Velocidad" to "Speed",
        "Combustible" to "Fuel",
        "Odómetro" to "Odometer",
        "Mi Perfil" to "My Profile",
        "Resumen de Registro" to "Registration Summary",
        "Información de Sesión Activa" to "Active Session Information",
        "Datos de Identidad" to "Identity Information",
        "Datos de Ubicación y Contacto" to "Location and Contact Information",
        "Documento de Identidad" to "Identity Document",
        "Documento" to "Document",
        "Teléfono" to "Phone",
        "Teléfono de Contacto" to "Contact Phone",
        "Dirección" to "Address",
        "Nombre Completo" to "Full Name",
        "Nombres" to "First Name",
        "Apellidos" to "Last Name",
        "Nombre del Taller o Mecánico" to "Workshop or Mechanic Name",
        "Nombre Comercial / Marca" to "Business Name / Brand",
        "Razón Social" to "Legal Business Name",
        "RUC (11 dígitos)" to "Tax ID (11 digits)",
        "Rol" to "Role",
        "Cliente" to "Customer",
        "Vehículo" to "Vehicle",
        "Marca" to "Make",
        "Modelo" to "Model",
        "Año" to "Year",
        "Placa" to "License Plate",
        "Número de Chasis (VIN - 17 dígitos)" to "Vehicle Identification Number (VIN - 17 digits)",
        "VIN (17 caracteres)" to "VIN (17 characters)",
        "Kilometraje Exacto *" to "Exact Mileage *",
        "Guardar Registro en Mi Garaje" to "Save Vehicle to My Garage",
        "Confirmar y Registrar Vehículo" to "Confirm and Register Vehicle",
        "Este es el vehículo principal de tu cuenta y no se puede eliminar." to "This is the primary vehicle on your account and cannot be deleted.",
        "¿Seguro que quieres eliminar este vehículo de tu garaje?" to "Are you sure you want to remove this vehicle from your garage?",
        "Eliminar vehículo" to "Delete Vehicle",
        "Mis Vehículos" to "My Vehicles",
        "Mis vehículos" to "My Vehicles",
        "Vehículos Registrados" to "Registered Vehicles",
        "Vehículos registrados" to "Registered Vehicles",
        "No tienes órdenes en taller ahora." to "You have no active workshop orders.",
        "Alertas del vehículo" to "Vehicle Alerts",
        "Diagnóstico OBD-II" to "OBD-II Diagnostics",
        "Mis Talleres" to "My Workshops",
        "Talleres Certificados" to "Certified Workshops",
        "Talleres Recomendados" to "Recommended Workshops",
        "Buscar Taller" to "Find a Workshop",
        "Buscar Taller Cercano para Reparación" to "Find a Nearby Repair Workshop",
        "Buscar taller por distrito o servicio..." to "Search workshops by district or service...",
        "Buscar por nombre o categoría..." to "Search by name or category...",
        "Aún no tienes talleres registrados" to "You don't have any workshops registered yet",
        "Este taller no tiene sedes registradas" to "This workshop has no branches registered",
        "Especialistas certificados" to "Certified Specialists",
        "Abierto ahora" to "Open Now",
        "Cómo llegar" to "Get Directions",
        "Agendar Cita en este Taller" to "Book an Appointment at This Workshop",
        "Confirmar Reserva de Cita" to "Confirm Appointment Booking",
        "Continuar a Confirmación" to "Continue to Confirmation",
        "Datos de la Cita" to "Appointment Details",
        "Selecciona la Fecha" to "Select a Date",
        "Elegir Horario" to "Choose a Time",
        "Selecciona la Bahía Técnica" to "Select a Service Bay",
        "Reservar Cita" to "Book Appointment",
        "Mis Citas" to "My Appointments",
        "Agenda de Citas B2B" to "B2B Appointment Schedule",
        "Agenda y Citas (Fleet)" to "Schedule and Appointments (Fleet)",
        "No hay citas registradas" to "No appointments registered",
        "Mantener Cita" to "Keep Appointment",
        "Cancelar y volver a Mi Garaje" to "Cancel and Return to My Garage",
        "¿Cancelar esta Cita?" to "Cancel this appointment?",
        "Turno No Disponible" to "Time Slot Unavailable",
        "Sede No Disponible" to "Branch Unavailable",
        "Horarios Libres de Turno" to "Available Time Slots",
        "Próximo Turno Libre" to "Next Available Time Slot",
        "Bahías libres hoy" to "Available Bays Today",
        "La bahía técnica asignada quedará liberada para otros conductores." to "The assigned service bay will be released for other drivers.",
        "El horario y bahía seleccionados acaban de ser reservados por otro conductor (409 Conflict)." to "The selected time and bay were just booked by another driver (409 Conflict).",
        "Equipo y Personal de Taller (Fleet)" to "Workshop Team and Staff (Fleet)",
        "Registrar Personal" to "Register Staff",
        "Asigna un empleado a esta sede especificando su especialidad y salario." to "Assign an employee to this branch by specifying their specialty and salary.",
        "Especialidad" to "Specialty",
        "Salario" to "Salary",
        "Solicitudes" to "Requests",
        "Talleres y Sedes" to "Workshops and Branches",
        "Gestionar Talleres y Sedes" to "Manage Workshops and Branches",
        "Sedes del Taller" to "Workshop Branches",
        "Seleccionar Sede" to "Select Branch",
        "Nueva Sede" to "New Branch",
        "Registrar Nueva Sede" to "Register New Branch",
        "Nombre de la Sede" to "Branch Name",
        "Código de Sede" to "Branch Code",
        "Dirección física" to "Street Address",
        "Teléfono de Contacto (+51)" to "Contact Phone (+51)",
        "Ingresar a esta sede" to "Enter This Branch",
        "Completa tu Perfil de Dueño" to "Complete Your Owner Profile",
        "Ver / Editar Mi Perfil de Dueño" to "View / Edit My Owner Profile",
        "Completa tu información legal y de contacto para asociar tus talleres en ShiftIQ." to "Complete your legal and contact information to link your workshops to ShiftIQ.",
        "Ingresa los datos de tu empresa para la facturación y presencia de tu taller." to "Enter your business details for workshop billing and public information.",
        "Para poder crear y administrar tus talleres, primero debes registrar tus datos de contacto y documento de identidad." to "To create and manage workshops, first register your contact details and identity document.",
        "Registra tu taller principal para comenzar a administrar sedes, personal y citas." to "Register your main workshop to start managing branches, staff, and appointments.",
        "Inventario y Productos" to "Inventory and Products",
        "Inventario vacío" to "Inventory is empty",
        "Ajustar Stock" to "Adjust Stock",
        "Stock crítico" to "Low Stock",
        "Stock mínimo de alerta *" to "Minimum Alert Stock *",
        "EXISTENCIAS" to "INVENTORY",
        "EXISTENCIAS ACTUALES" to "CURRENT INVENTORY",
        "Código SKU *" to "SKU Code *",
        "Nombre del repuesto *" to "Part Name *",
        "Descripción" to "Description",
        "Costo unitario (S/)" to "Unit Cost (S/)",
        "Precio de venta (S/) *" to "Sale Price (S/) *",
        "Mínimo de alerta" to "Minimum Alert Level",
        "No se encontraron productos para esta sucursal o con el filtro seleccionado." to "No products were found for this branch or selected filter.",
        "No hay lotes con existencias disponibles." to "There are no batches with available stock.",
        "Elimina el repuesto del catálogo. Solo es posible si no está vinculado a órdenes de trabajo." to "Remove the part from the catalog. This is only possible if it is not linked to work orders.",
        "Eliminar repuesto" to "Delete Part",
        "¿Eliminar repuesto?" to "Delete part?",
        "El nuevo lote se agregará al final de la cola FIFO." to "The new batch will be added to the end of the FIFO queue.",
        "El descuento aplica la política FIFO sobre los lotes más antiguos." to "The deduction follows the FIFO policy and uses the oldest batches first.",
        "Órdenes de Trabajo (OTs)" to "Work Orders",
        "Órdenes Trabajo" to "Work Orders",
        "Órdenes" to "Orders",
        "Orden de Trabajo" to "Work Order",
        "Nueva Orden de Trabajo" to "New Work Order",
        "Orden de Trabajo no encontrada" to "Work order not found",
        "Orden no encontrada" to "Work order not found",
        "Detalle de OT" to "Work Order Details",
        "Nueva OT" to "New Work Order",
        "Diagnóstico Inicial / Motivo de Ingreso *" to "Initial Diagnosis / Reason for Visit *",
        "Descripción del Servicio" to "Service Description",
        "Agregar Tarea Mecánica" to "Add Mechanical Task",
        "Mecánico Asignado" to "Assigned Mechanic",
        "Observación del Técnico" to "Technician Notes",
        "Insumos y Repuestos Utilizados" to "Supplies and Parts Used",
        "Presupuesto Estimado" to "Estimated Quote",
        "Costo Total Invertido (PEN)" to "Total Cost Invested (PEN)",
        "Costo Total Liquidado" to "Total Settled Cost",
        "Emitir Boleta / Factura SUNAT" to "Issue SUNAT Receipt / Invoice",
        "EMISIÓN SUNAT VÁLIDA" to "VALID SUNAT ISSUE",
        "Comprobante de servicio" to "Service Receipt",
        "Reporte PDF" to "PDF Report",
        "Descargar Informe PDF" to "Download PDF Report",
        "Descargar Archivo PDF en Dispositivo ↓" to "Save PDF to Device ↓",
        "Compartir por Correo o WhatsApp" to "Share by Email or WhatsApp",
        "¡Archivo PDF Guardado!" to "PDF Saved!",
        "El comprobante se guardó correctamente en la ubicación que elegiste." to "The receipt was saved to the location you selected.",
        "Archivo disponible en la ubicación seleccionada" to "File available at the selected location",
        "No hay un PDF adjunto en el backend; puedes guardar una copia con los datos del comprobante." to "There is no PDF attached in the backend; you can save a copy using the receipt details.",
        "Diagnóstico del vehículo" to "Vehicle Diagnostics",
        "DIAGNÓSTICO DEL VEHÍCULO" to "VEHICLE DIAGNOSTICS",
        "Ver Diagnóstico Completo" to "View Full Diagnostics",
        "Ver Orden de Trabajo en Vivo" to "View Live Work Order",
        "Telemetría en Tiempo Real" to "Real-Time Telemetry",
        "TELEMETRÍA EN VIVO" to "LIVE TELEMETRY",
        "Historial de telemetría" to "Telemetry History",
        "Última lectura" to "Latest Reading",
        "Detalle de lecturas" to "Reading Details",
        "Cargando lecturas…" to "Loading readings…",
        "Cargar más lecturas" to "Load More Readings",
        "Aún no hay lecturas anteriores para graficar." to "There are no previous readings to chart yet.",
        "Aún no hay lecturas de telemetría para este vehículo." to "There are no telemetry readings for this vehicle yet.",
        "Sin alertas reportadas" to "No alerts reported",
        "Sin códigos de falla detectados" to "No fault codes detected",
        "Escanear Dispositivos Cercanos" to "Scan Nearby Devices",
        "Conectar Dispositivo" to "Connect Device",
        "Sincronización Bluetooth OBD-II" to "OBD-II Bluetooth Sync",
        "Conecta el escáner Bluetooth OBD-II." to "Connect the OBD-II Bluetooth scanner.",
        "Configura el perfil de tu auto y sincroniza el escáner Bluetooth OBD-II." to "Set up your car profile and sync the OBD-II Bluetooth scanner.",
        "Escaneo OBD-II en Curso" to "OBD-II Scan in Progress",
        "Detener Escaneo" to "Stop Scan",
        "Iniciar Escaneo Completo" to "Start Full Scan",
        "Inicia el escaneo" to "Start Scan",
        "Modo Offline" to "Offline Mode",
        "Almacenamiento Local Activo" to "Local Storage Active",
        "Forzar Sincronización con la Nube" to "Force Cloud Sync",
        "Hito 1: IAM completado" to "Milestone 1: IAM completed",
        "Panel" to "Dashboard",
        "Panel Ejecutivo del Taller · ShiftIQ" to "Workshop Executive Dashboard · ShiftIQ",
        "Panel Operativo de Bahía · ShiftIQ" to "Service Bay Operations Dashboard · ShiftIQ",
        "Información de Sesión Activa" to "Active Session Information",
        "Estado del Token" to "Token Status",
        "Sucursal Activa" to "Active Branch",
        "ID de Usuario" to "User ID",
        "Rol del Sistema" to "System Role",
        "Actualiza" to "Refresh",
        "Actualizar" to "Refresh",
        "No se pudo crear la cuenta" to "Could not create the account",
        "Verifica tu Correo Electrónico" to "Verify Your Email Address",
        "Reenviar código" to "Resend Code",
        "¿No recibiste el código?" to "Didn't receive the code?",
        "Ingresa el Código OTP" to "Enter the OTP Code",
        "Corregir Datos" to "Edit Details",
        "Revisa los detalles técnicos antes de crear tu cuenta ShiftIQ." to "Review the technical details before creating your ShiftIQ account.",
        "Tu contraseña ha sido restablecida exitosamente. Ahora puedes iniciar sesión en ShiftIQ con tus nuevas credenciales." to "Your password has been reset. You can now sign in to ShiftIQ with your new credentials.",
        "¡Contraseña Actualizada!" to "Password Updated!",
        "¡Cuenta y Vehículo Registrados!" to "Account and Vehicle Registered!",
        "¡Cita Confirmada con Éxito!" to "Appointment Confirmed!",
        "Acepto los Términos de Servicio y el Tratamiento de Datos Telemáticos del Vehículo." to "I accept the Terms of Service and the processing of vehicle telemetry data.",
        "Acepto recibir notificaciones automáticas de fallas (DTC) y telemetría diagnóstica vía WhatsApp." to "I agree to receive automatic fault (DTC) and diagnostic telemetry notifications via WhatsApp.",
        "Requisitos de seguridad:" to "Security requirements:",
        "¿Necesitas ayuda? Contacta a soporte" to "Need help? Contact support",
        "Vehículos" to "Vehicles",
        "Talleres" to "Workshops",
        "Citas" to "Appointments",
        "Inicio" to "Home",
        "Todo" to "All",
        "Todas" to "All",
        "Todos" to "All",
        "Pendientes" to "Pending",
        "Completadas" to "Completed",
        "Canceladas" to "Canceled",
        "En Progreso" to "In Progress",
        "Registrar" to "Register",
        "Registros certificados y facturas" to "Certified records and invoices",
        "Servicios y Especialidades" to "Services and Specialties",
        "Notas o Motivo del Mantenimiento (Opcional)" to "Notes or Maintenance Reason (Optional)",
        "Comentarios o Síntomas para el Mecánico" to "Comments or Symptoms for the Mechanic",
        "Mantén tu historial vehicular al día" to "Keep your vehicle history up to date",
        "Tu auto está en reparación" to "Your car is being repaired",
        "Protocolo de Operaciones Ejecutadas" to "Completed Operations Protocol",
        "Valores Técnicos y Calibración" to "Technical Values and Calibration",
        "Impacto en la Conducción" to "Impact on Driving",
        "Especificación Técnica OEM" to "OEM Technical Specification",
        "Vincular OBD2" to "Pair OBD2",
        "Vincular y Continuar" to "Pair and Continue",
        "Volver a Escanear" to "Scan Again",
        "Continuar a Datos del Vehículo" to "Continue to Vehicle Details",
        "Continuar a Vehículo y OBD-II" to "Continue to Vehicle and OBD-II",
        "Datos del Propietario" to "Owner Information",
        "Paso 1: Datos del Propietario" to "Step 1: Owner Information",
        "Paso 2: Especificaciones y Dongle IoT" to "Step 2: Specifications and IoT Dongle",
        "Fecha del Servicio *" to "Service Date *",
        "Tipo de Servicio Realizado *" to "Service Type *",
        "Adjuntar Foto del Vehículo" to "Attach Vehicle Photo",
        "Adjuntar Comprobante o Factura (Opcional)" to "Attach Receipt or Invoice (Optional)",
        "Subir fotografía frontal o lateral (Opcional)" to "Upload a front or side photo (Optional)",
        "Repuesto" to "Part",
        "Unidad" to "Unit",
        "Cantidad *" to "Quantity *",
        "Nombre del Repuesto *" to "Part Name *",
        "Servicio" to "Service",
        "Detalle" to "Details",
        "Ver Factura / Boleta Digital (PDF)" to "View Digital Invoice / Receipt (PDF)",
        "Ver en Mis Citas" to "View in My Appointments",
        "Cambiar" to "Change",
        "Continuar a Confirmación" to "Continue to Confirmation",
        "Todos los años" to "All Years",
        "Todos los sistemas ECU operan dentro de los parámetros OEM normales." to "All ECU systems are operating within normal OEM parameters.",
        "Especialidad" to "Specialty",
        "Filtrar" to "Filter",
        "Guardar Cambios" to "Save Changes",
        "Crear Taller" to "Create Workshop",
        "Crear Sede" to "Create Branch",
        "Dirección y Contacto" to "Address and Contact",
        "Datos del Vehículo" to "Vehicle Information",
        "Información del Vehículo" to "Vehicle Information",
        "Resumen de Cita Técnica" to "Technical Appointment Summary",
        "Fecha y hora" to "Date and Time",
        "Turno disponible" to "Available Time Slot",
        "No hay servicios activos" to "No active services",
        "Sede seleccionada" to "Selected Branch",
        "Activa" to "Active",
        "Disponible" to "Available",
        "Recomendado" to "Recommended",
        "Red certificada" to "Certified Network",
        "Compatibilidad Certificada" to "Certified Compatibility",
        "Vínculo OBD-II BLE 5.2 Activo" to "OBD-II BLE 5.2 Connection Active",
        "Sin Dispositivo" to "No Device",
        "Sincronización Bluetooth OBD-II" to "OBD-II Bluetooth Sync",
        "Progreso" to "Progress",
        "Año del vehículo" to "Vehicle Year",
        "DNI / Carné de Extranjería" to "National ID / Foreign Resident ID",
        "DNI / Cédula" to "National ID",
        "Nombre Completo *" to "Full Name *",
        "Placa *" to "License Plate *",
        "Marca y Modelo *" to "Make and Model *",
        "Kilometraje Ingreso *" to "Mileage at Check-In *",
        "Documento de Identidad *" to "Identity Document *",
        "Número de teléfono" to "Phone Number",
        "Política de agenda: Tolerancia máxima de 10 minutos para recepción en bahía." to "Booking policy: a maximum 10-minute grace period for service bay check-in.",
        "Garantía ShiftIQ: Sin cobros por adelantado. Pago presencial en caja del taller al retirar el vehículo." to "ShiftIQ guarantee: No upfront charges. Pay at the workshop when picking up your vehicle.",
        "La sede seleccionada se encuentra en mantenimiento. Puedes agendar en estas sedes alternativas:" to "The selected branch is under maintenance. You can book at these alternative branches:",
        "La cantidad supera las existencias disponibles." to "The quantity exceeds the available stock.",
        "El switch de encendido debe estar en posición ON sin arrancar el motor." to "The ignition must be in the ON position without starting the engine.",
        "Enciende el contacto del vehículo para activar." to "Turn the vehicle ignition on to activate.",
        "Asegúrate de que el adaptador BLE esté encendido" to "Make sure the BLE adapter is powered on",
        "Severidad" to "Severity",
        "Prioridad" to "Priority",
        "Última lectura" to "Latest Reading",
        "Sin servicios activos" to "No active services",
        "Archivo PDF Guardado" to "PDF Saved",
        "Vinculado a falla DTC" to "Linked to DTC Fault",
        "VINCULADO A FALLA DTC" to "LINKED TO DTC FAULT",
        "Información Fiscal y Comercial" to "Tax and Business Information",
        "Historial de Intervenciones" to "Service History",
        "Registro de Intervenciones" to "Service History",
        "Ver Perfil" to "View Profile",
        "Editar Información" to "Edit Information",
        "Ver / Editar Mi Perfil de Dueño" to "View / Edit My Owner Profile",
        "Marca" to "Make",
        "Guardar Registro en Mi Garaje" to "Save to My Garage",
        "Protocolo de Operaciones Ejecutadas" to "Completed Operations Protocol",
        "Administrar" to "Manage",
        "Añadir vehículo" to "Add Vehicle",
        "Cancelar Cita" to "Cancel Appointment",
        "Actualizar Perfil" to "Update Profile",
        "Buscar taller" to "Search Workshops",
        "Esperando confirmación" to "Awaiting Confirmation",
        "No hay talleres disponibles" to "No workshops available",
        "No hay vehículos registrados" to "No vehicles registered",
        "Inicio de sesión" to "Sign In",
        "Registrarse" to "Create Account",
        "¿Ya tienes cuenta?" to "Already have an account?",
        "Estado" to "Status",
        "Fecha" to "Date",
        "Hora" to "Time",
        "Precio" to "Price",
        "Total" to "Total",
        "Notas" to "Notes",
        "Detalles" to "Details",
        "Ubicación" to "Location",
        "Abierto" to "Open",
        "Cerrado" to "Closed",
        "En servicio" to "In Service",
        "Mantenimiento" to "Maintenance",
        "Cancelar y volver a Mi Garaje" to "Cancel and return to My Garage",
        "Activa tu cuenta" to "Activate your account",
        "Confirmar contraseña" to "Confirm password",
        "Nueva contraseña" to "New password",
        "Cambiar contraseña" to "Change password",
        "Enviar código" to "Send Code",
        "Reenviar código" to "Resend Code",
        "Ingresar Lote" to "Add Batch",
        "Módulos Inspeccionados" to "Inspected Modules",
        "Códigos de Falla" to "Fault Codes",
        "Código de falla" to "Fault Code",
        "Últimas lecturas" to "Latest Readings",
        "Telemetría" to "Telemetry",
        "Cita" to "Appointment",
        "Taller" to "Workshop",
        "Sede" to "Branch",
        "Empleado" to "Employee",
        "Propietario" to "Owner",
        "Personal" to "Staff",
        "Horas" to "Hours",
        "Vehículo principal" to "Primary Vehicle",
        "Vehículo agregado correctamente" to "Vehicle added successfully",
        "El vehículo principal no se puede eliminar" to "The primary vehicle cannot be deleted",
        "Guardar cambios" to "Save Changes",
        "Volver al inicio de sesión" to "Return to Sign In",
        "Vincular y continuar" to "Pair and Continue",
        "Lista de talleres" to "Workshop List",
        "Buscar taller por distrito o servicio..." to "Search workshops by district or service...",
        "Aún no hay talleres registrados" to "No workshops registered yet",
        "No se encontraron talleres" to "No workshops found",
        "((•)) LÁSER 3D" to "((•)) 3D LASER",
        "17 caracteres. Ubicado en la tarjeta de propiedad o base del parabrisas." to "17 characters. Find it on the vehicle registration card or at the base of the windshield.",
        "Aceptar y Salir" to "Accept and Sign Out",
        "Activadas" to "Enabled",
        "Agenda una nueva cita para tu sede usando el botón '+' inferior." to "Schedule a new appointment for your branch using the '+' button below.",
        "Alineado para resolver falla DTC P0135 (Sensor O2)" to "Configured to address DTC P0135 (O2 Sensor)",
        "Asegúrate de cumplir con los requisitos de seguridad para proteger tu cuenta de ShiftIQ." to "Make sure you meet the security requirements to protect your ShiftIQ account.",
        "Autenticación, persistencia en DataStore y control de acceso basados en rol listos para los siguientes bloques de negocio." to "Authentication, DataStore persistence, and role-based access control are ready for the next business modules.",
        "Automática" to "Automatic",
        "Bienvenido a ShiftIQ. Tu vehículo y protocolo de telemetría han sido vinculados correctamente. Ya puedes iniciar sesión con tu cuenta." to "Welcome to ShiftIQ. Your vehicle and telemetry protocol have been linked successfully. You can now sign in to your account.",
        "Buscando señal BLE..." to "Searching for a BLE signal...",
        "Cada sede física atiende citas, gestiona repuestos y emite comprobantes." to "Each physical branch handles appointments, manages parts, and issues receipts.",
        "Cada taller necesita al menos una sede física con dirección y teléfono para atender órdenes de trabajo." to "Each workshop needs at least one physical branch with an address and phone number to handle work orders.",
        "Cambiar ▼" to "Change ▼",
        "Cerrar ventana" to "Close window",
        "Certificado Digital ShiftIq • Garantía 6 meses o 5,000 km" to "ShiftIQ Digital Certificate • 6-month or 5,000-km warranty",
        "Certificado ShiftIq" to "ShiftIQ Certificate",
        "Consultando DTC en Banco 1 Emisiones..." to "Checking DTCs for Bank 1 emissions...",
        "Crea tu Nueva Contraseña" to "Create Your New Password",
        "DESCRIPCIÓN / ÍTEM" to "DESCRIPTION / ITEM",
        "Deseo recibir reportes y avisos por SMS / WhatsApp" to "I want to receive reports and alerts by SMS / WhatsApp",
        "Dueño de Taller Verificado" to "Verified Workshop Owner",
        "Editar Datos Anteriores" to "Edit Previous Details",
        "Ej. Aceite sintético 5W-30 Shell Helix, filtro Bosch..." to "E.g. Shell Helix 5W-30 synthetic oil, Bosch filter...",
        "Ej. El testigo Check Engine parpadea al superar 60 km/h o en ralentí prolongado..." to "E.g. The Check Engine light flashes above 60 km/h or after prolonged idling...",
        "Ej. FLT-OIL-001" to "E.g. FLT-OIL-001",
        "Ej. Lubricentro San Antonio / Taller Particular" to "E.g. San Antonio Lube Center / Independent Workshop",
        "Ej: Alan" to "E.g. Alan",
        "Ej: AutoServicios Perú S.A.C." to "E.g. AutoServicios Perú S.A.C.",
        "Ej: Av. Benavides 1850, Surco, Lima" to "E.g. Av. Benavides 1850, Surco, Lima",
        "Ej: Jara" to "E.g. Jara",
        "Ej: SEDE-SUR-01" to "E.g. BRANCH-SOUTH-01",
        "Ej: Sede Surco Principal" to "E.g. Main Surco Branch",
        "Ej: ShiftIQ Taller Central" to "E.g. ShiftIQ Central Workshop",
        "Elevadores hidráulicos" to "Hydraulic Lifts",
        "Enlace de Dispositivo OBD-II" to "OBD-II Device Connection",
        "Escaneo Progresivo de Módulos" to "Progressive Module Scan",
        "Esta acción eliminará el producto y sus lotes. " to "This action will delete the product and its batches.",
        "Ficha Técnica del Vehículo" to "Vehicle Specifications",
        "Filtro activo: ISO 15765-4 (CAN-BUS)" to "Active filter: ISO 15765-4 (CAN-BUS)",
        "Flujo de Contingencia de Datos" to "Data Contingency Flow",
        "Formatos soportados: JPG, PNG, PDF (Máx. 10 MB)" to "Supported formats: JPG, PNG, PDF (10 MB max)",
        "Horarios alternativos inmediatos disponibles hoy:" to "These alternative time slots are available today:",
        "I.G.V. (18%):" to "VAT (18%):",
        "Ingresa el correo electrónico asociado a tu cuenta y te enviaremos un código de seguridad de 6 dígitos." to "Enter the email address associated with your account and we'll send you a 6-digit security code.",
        "Ingresa los identificadores de la sede, cliente y vehículo para registrar la cita en el sistema." to "Enter the branch, customer, and vehicle IDs to register the appointment in the system.",
        "Ingresa tus datos personales para asociar la titularidad del escáner y recibir reportes técnicos de telemetría." to "Enter your personal details to register the scanner under your name and receive technical telemetry reports.",
        "Intervalo sugerido de mantenimiento (km)" to "Suggested Maintenance Interval (km)",
        "Ir al Inicio de Sesión" to "Go to Sign In",
        "LAT: 12ms" to "LAT: 12 ms",
        "La telemetría se resguarda en el buffer SQLite (Room) local del teléfono sin pérdida de datos." to "Telemetry is stored in the phone's local SQLite (Room) buffer without data loss.",
        "Lectura" to "Reading",
        "Lectura con alerta DTC" to "Reading with DTC Alert",
        "Modificar Datos" to "Edit Details",
        "Monitoreo Activo" to "Monitoring Active",
        "No tasks have been registered for this work order yet." to "No tasks have been registered for this work order yet.",
        "Notificaciones Push DTC" to "DTC Push Notifications",
        "OBD2 Enlazado" to "OBD2 Paired",
        "ODÓMETRO REGISTRADO" to "RECORDED ODOMETER",
        "Op. Gravada:" to "Taxable Amount:",
        "Opcional: puedes continuar sin dongle y vincularlo después desde el garaje." to "Optional: you can continue without a dongle and pair it later from the garage.",
        "PDF Comprobante" to "PDF Receipt",
        "PDF/A • 218 KB" to "PDF/A • 218 KB",
        "PNG, JPG hasta 5MB" to "PNG, JPG up to 5 MB",
        "PÓLIZA DE COBERTURA" to "WARRANTY POLICY",
        "Página 1 de 1" to "Page 1 of 1",
        "Registra factura y kilometraje manual" to "Enter the invoice and mileage manually",
        "Registra los mantenimientos preventivos o correctivos realizados en talleres externos independientes para sincronizar el odómetro telemático y las alertas de vida de aceite." to "Record preventive or corrective maintenance performed at independent workshops to sync the telemetry odometer and oil-life alerts.",
        "Registrar Datos del Dueño" to "Register Owner Details",
        "Repuestos o notas de la intervención" to "Parts or service notes",
        "Requerido para comprobantes fiscales de servicio" to "Required for service tax receipts",
        "Scanning for nearby BLE / bonded OBD adapters…" to "Searching for nearby BLE / paired OBD adapters…",
        "Se cerrará tu sesión activa de conductor en este dispositivo. Tendrás que volver a ingresar tu correo y contraseña." to "Your active driver session on this device will be signed out. You'll need to enter your email and password again.",
        "Sync: Hace 3m" to "Sync: 3m ago",
        "Sí, Cancelar Cita" to "Yes, Cancel Appointment",
        "Tasks and evidence" to "Tasks and Evidence",
        "The backend did not return line items for this voucher." to "The backend did not return line items for this voucher.",
        "Tipo: Descontar existencias" to "Type: Deduct Stock",
        "Total Invertido" to "Total Invested",
        "Tramas Recientes en Cola" to "Recent Frames in Queue",
        "Tus datos y telemetría están protegidos mediante cifrado CAN-BUS seguro." to "Your data and telemetry are protected with secure CAN-BUS encryption.",
        "VALORIZACIÓN ALMACÉN" to "INVENTORY VALUATION",
        "VEHÍCULO / PLACA" to "VEHICLE / LICENSE PLATE",
        "VEHÍCULO ASIGNADO" to "ASSIGNED VEHICLE",
        "Velocidad de bus: 500 kbps (11-bit identifier)" to "Bus speed: 500 kbps (11-bit identifier)",
        "Verificado con el estándar ISO 3779" to "Verified against ISO 3779",
        "Verificar y Crear Cuenta" to "Verify and Create Account",
        "Versión de ShiftIQ" to "ShiftIQ Version",
        "Volver a Datos de Confirmación" to "Back to Confirmation Details",
        "Volver al catálogo" to "Back to Catalog",
        "[ + Tomar Foto o Subir Archivo ]" to "[ + Take Photo or Upload File ]",
        "[ EN SERVICIO ACTIVO ]" to "[ ACTIVE SERVICE ]",
        "[ Explorar archivo ]" to "[ Browse Files ]",
        "v1.0.0 (B2C)" to "v1.0.0 (B2C)",
        "¡Vehículo y Telemetría Listos!" to "Vehicle and Telemetry Ready!",
        "¿Qué significa en lenguaje sencillo?" to "What does this mean in plain language?",
        "¿Realizaste un servicio fuera de red?" to "Did you get service outside the network?",
        "¿Ya tienes cuenta? " to "Already have an account? ",
        "¿Ya tienes una cuenta? " to "Already have an account? ",
        "✓ Correo válido" to "✓ Valid email",
        "✓ Las contraseñas coinciden" to "✓ Passwords match",
        "Liquidación y facturación" to "Billing and checkout",
        "Emisión de comprobante interno" to "Internal voucher issuance",
        "ORDEN DE TRABAJO" to "WORK ORDER",
        "TIPO DE COMPROBANTE" to "VOUCHER TYPE",
        "Boleta de venta" to "Sales receipt",
        "Factura" to "Invoice",
        "DATOS DEL CLIENTE Y DOCUMENTO" to "CUSTOMER AND DOCUMENT DETAILS",
        "Nombre / razón social" to "Name / legal business name",
        "Número de documento" to "Document number",
        "Dirección fiscal" to "Fiscal address",
        "DETALLE DE SERVICIOS Y REPUESTOS EN BAHÍA" to "BAY SERVICES AND PARTS",
        "Cant:" to "Qty:",
        "Subtotal" to "Subtotal",
        "IGV (18%)" to "VAT (18%)",
        "MÉTODO DE PAGO Y COBRO EN CAJA" to "PAYMENT METHOD AND COUNTER COLLECTION",
        "Efectivo en Caja" to "Cash at counter",
        "Tarjeta de Crédito" to "Credit card",
        "Tarjeta de Débito" to "Debit card",
        "Transferencia Bancaria" to "Bank transfer",
        "Cotización aprobada" to "Approved quote",
        "No hay cotizaciones aprobadas disponibles para emitir un comprobante." to "No approved quotes are available to issue a voucher.",
        "La cotización no contiene conceptos detallados." to "The quote has no item details.",
        "El comprobante se emite desde la cotización aprobada seleccionada." to "The voucher is issued from the selected approved quote.",
        "Ingresa el nombre o razón social." to "Enter the name or legal business name.",
        "Ingresa el número de documento." to "Enter the document number.",
        "DNI requiere 8 dígitos." to "DNI must contain 8 digits.",
        "RUC requiere 11 dígitos." to "RUC must contain 11 digits.",
        "La factura requiere un RUC válido." to "An invoice requires a valid RUC.",
        "Emitir comprobante" to "Issue voucher",
        "EMITIR BOLETA" to "ISSUE RECEIPT",
        "EMITIR FACTURA" to "ISSUE INVOICE",
        "Emitiendo comprobante…" to "Issuing voucher…"
    )

    @Synchronized
    fun initialize(context: Context) {
        if (initialized) return

        runCatching {
            val es = readAsset(context, "localization/app_es.arb")
            val en = readAsset(context, "localization/app_en.arb")
            val iterator = es.keys()
            while (iterator.hasNext()) {
                val key = iterator.next()
                if (key.startsWith("@") || !en.has(key)) continue
                val spanish = es.opt(key) as? String ?: continue
                val english = en.opt(key) as? String ?: continue
                spanishToEnglish[spanish] = english
                englishToSpanish[english] = spanish
            }
        }

        manualPairs.forEach { (spanish, english) ->
            spanishToEnglish[spanish] = english
            englishToSpanish[english] = spanish
        }
        initialized = true
    }

    fun translate(text: String, language: AppLanguage): String {
        if (language == AppLanguage.ENGLISH) {
            return spanishToEnglish[text] ?: translatePattern(text, language) ?: text
        }
        return englishToSpanish[text] ?: translatePattern(text, language) ?: text
    }

    private fun readAsset(context: Context, path: String): JSONObject =
        context.assets.open(path).bufferedReader(Charsets.UTF_8).use { JSONObject(it.readText()) }

    private fun translatePattern(text: String, language: AppLanguage): String? {
        if (language == AppLanguage.ENGLISH) {
            Regex("^Bahía (\\d+)$").matchEntire(text)?.let { return "Bay " + it.groupValues[1] }
            Regex("^A (.+) km de tu ubicación$").matchEntire(text)?.let {
                return "About " + it.groupValues[1] + " km from your location"
            }
            Regex("^Hola, (.+)\\. Revisa aquí el estado de tus autos\\.$").matchEntire(text)?.let {
                return "Hi, " + it.groupValues[1] + ". Check your vehicles' status here."
            }
            Regex("^Código (.+)$").matchEntire(text)?.let { return "Code " + it.groupValues[1] }
            Regex("^Placa: (.+)$").matchEntire(text)?.let { return "License Plate: " + it.groupValues[1] }
            Regex("^SKU: (.+)$").matchEntire(text)?.let { return "SKU: " + it.groupValues[1] }
            Regex("^RUC: (.+)$").matchEntire(text)?.let { return "Tax ID: " + it.groupValues[1] }
            Regex("^DNI: (.+)$").matchEntire(text)?.let { return "ID: " + it.groupValues[1] }
            Regex("^Garantía (.+) días$").matchEntire(text)?.let { return it.groupValues[1] + "-day warranty" }
            Regex("^Actualizado (.+)$").matchEntire(text)?.let { return "Updated " + it.groupValues[1] }
            Regex("^Canceladas \\((.+)\\)$").matchEntire(text)?.let { return "Canceled (" + it.groupValues[1] + ")" }
            Regex("^Completadas \\((.+)\\)$").matchEntire(text)?.let { return "Completed (" + it.groupValues[1] + ")" }
            Regex("^Pendientes \\((.+)\\)$").matchEntire(text)?.let { return "Pending (" + it.groupValues[1] + ")" }
            Regex("^En progreso \\((.+)\\)$").matchEntire(text)?.let { return "In progress (" + it.groupValues[1] + ")" }
            Regex("^Solicitudes \\((.+)\\)$").matchEntire(text)?.let { return "Requests (" + it.groupValues[1] + ")" }
            Regex("^Todas \\((.+)\\)$").matchEntire(text)?.let { return "All (" + it.groupValues[1] + ")" }
            Regex("^Todos \\((.+)\\)$").matchEntire(text)?.let { return "All (" + it.groupValues[1] + ")" }
            Regex("^Activos \\((.+)\\)$").matchEntire(text)?.let { return "Active (" + it.groupValues[1] + ")" }
            Regex("^Hola, (.+)$").matchEntire(text)?.let { return "Hi, " + it.groupValues[1] }
            Regex("^Estado: (.+)$").matchEntire(text)?.let { return "Status: " + it.groupValues[1] }
            Regex("^Servicio: (.+)$").matchEntire(text)?.let { return "Service: " + it.groupValues[1] }
            Regex("^Motivo: (.+)$").matchEntire(text)?.let { return "Reason: " + it.groupValues[1] }
            Regex("^Ubicación: (.+)$").matchEntire(text)?.let { return "Location: " + it.groupValues[1] }
            Regex("^Mín\\. (.+) · Máx\\. (.+)$").matchEntire(text)?.let {
                return "Min. " + it.groupValues[1] + " · Max. " + it.groupValues[2]
            }
            Regex("^(.+) Bahías Activas$").matchEntire(text)?.let { return it.groupValues[1] + " Active Bays" }
            Regex("^(.+) Bahías activas$").matchEntire(text)?.let { return it.groupValues[1] + " active bays" }
            Regex("^(.+) módulos inspeccionados$").matchEntire(text)?.let { return it.groupValues[1] + " modules inspected" }
            Regex("^(.+) tramas en cola local$").matchEntire(text)?.let { return it.groupValues[1] + " frames in local queue" }
            Regex("^(.+) un\\.$").matchEntire(text)?.let { return it.groupValues[1] + " units" }
            Regex("^(.+) Técnicos$").matchEntire(text)?.let { return it.groupValues[1] + " Technicians" }
            Regex("^\\((.+) reseñas\\)$").matchEntire(text)?.let { return "(" + it.groupValues[1] + " reviews)" }
            Regex("^Documento: (.+)$").matchEntire(text)?.let { return "Document: " + it.groupValues[1] }
            Regex("^Cliente: (.+)$").matchEntire(text)?.let { return "Customer: " + it.groupValues[1] }
            Regex("^Cliente (.+) · Vehículo (.+)$").matchEntire(text)?.let {
                return "Customer ${it.groupValues[1]} · Vehicle ${it.groupValues[2]}"
            }
            Regex("^Correo actual: (.+)$").matchEntire(text)?.let { return "Current email: " + it.groupValues[1] }
            Regex("^Fecha: (.+)$").matchEntire(text)?.let { return "Date: " + it.groupValues[1] }
            Regex("^Empleado: (.+)$").matchEntire(text)?.let { return "Employee: " + it.groupValues[1] }
            Regex("^Pagos: (.+)$").matchEntire(text)?.let { return "Payments: " + it.groupValues[1] }
            Regex("^Ingreso: (.+)$").matchEntire(text)?.let { return "Check-in: " + it.groupValues[1] }
            Regex("^Iniciado: (.+)$").matchEntire(text)?.let { return "Started: " + it.groupValues[1] }
            Regex("^Inicial: (.+) • Disponible: (.+)$").matchEntire(text)?.let {
                return "Initial: " + it.groupValues[1] + " • Available: " + it.groupValues[2]
            }
            Regex("^Tamaño: (.+) • Formato: (.+)$").matchEntire(text)?.let {
                return "Size: " + it.groupValues[1] + " • Format: " + it.groupValues[2]
            }
            Regex("^Historial\\s+(.+)$").matchEntire(text)?.let { return "History " + it.groupValues[1] }
            Regex("^Próximas\\s+(.+)$").matchEntire(text)?.let { return "Upcoming " + it.groupValues[1] }
            Regex("^Salud (.+)%$").matchEntire(text)?.let { return "Health " + it.groupValues[1] + "%" }
            Regex("^Señal (.+)$").matchEntire(text)?.let { return "Signal " + it.groupValues[1] }
            Regex("^Severidad: (.+) • Revisión sugerida en (.+) km$").matchEntire(text)?.let {
                return "Severity: " + it.groupValues[1] + " • Suggested review in " + it.groupValues[2] + " km"
            }
            Regex("^Incrementará el stock de (.+) a (.+) unidades\\.$").matchEntire(text)?.let {
                return "Stock will increase from " + it.groupValues[1] + " to " + it.groupValues[2] + " units."
            }
            Regex("^El código expira en (.+)$").matchEntire(text)?.let { return "Code expires in " + it.groupValues[1] }
            Regex("^Hemos enviado un código de 6 dígitos a (.+)\\. Ingrésalo a continuación para verificar tu identidad\\.$").matchEntire(text)?.let {
                return "We sent a 6-digit code to " + it.groupValues[1] + ". Enter it below to verify your identity."
            }
            Regex("^Hemos enviado un código de 6 dígitos a (.+)\\. Ingrésalo a continuación para confirmar tu titularidad y crear tu cuenta\\.$").matchEntire(text)?.let {
                return "We sent a 6-digit code to " + it.groupValues[1] + ". Enter it below to confirm ownership and create your account."
            }
            Regex("^Estás a punto de cancelar la cita (.+) en (.+) programada para el (.+) a las (.+)\\.$").matchEntire(text)?.let {
                return "You are about to cancel appointment " + it.groupValues[1] + " at " + it.groupValues[2] + " scheduled for " + it.groupValues[3] + " at " + it.groupValues[4] + "."
            }
            Regex("^Tu auto está en reparación en (.+)$").matchEntire(text)?.let {
                return "Your car is being repaired at " + it.groupValues[1]
            }
            Regex("^(.+) evidence file\\(s\\)$").matchEntire(text)?.let {
                return it.groupValues[1] + " evidence file(s)"
            }
            Regex("^Assigned to (.+)$").matchEntire(text)?.let { return "Assigned to " + it.groupValues[1] }
            Regex("^(.+) Cercanos$").matchEntire(text)?.let { return it.groupValues[1] + " Nearby" }
            Regex("^(.+) lotes FIFO activos$").matchEntire(text)?.let { return it.groupValues[1] + " active FIFO batches" }
            Regex("^(.+) • (.+) Bahías activas$").matchEntire(text)?.let {
                return it.groupValues[1] + " • " + it.groupValues[2] + " active bays"
            }
            Regex("^(.+) · antigua$").matchEntire(text)?.let { return it.groupValues[1] + " · older" }
            Regex("^(.+) · reciente$").matchEntire(text)?.let { return it.groupValues[1] + " · newer" }
            Regex("^(.+) · revisar en (.+) km$").matchEntire(text)?.let {
                return it.groupValues[1] + " · review in " + it.groupValues[2] + " km"
            }
            Regex("^Componente a verificar: (.+)$").matchEntire(text)?.let {
                return "Component to inspect: " + it.groupValues[1]
            }
            Regex("^Código: (.+)$").matchEntire(text)?.let { return "Code: " + it.groupValues[1] }
            Regex("^DTC (.+) · (.+) · (.+)$").matchEntire(text)?.let {
                return "DTC " + it.groupValues[1] + " · " + it.groupValues[2] + " · " + it.groupValues[3]
            }
            Regex("^EXP-TEC (.+)$").matchEntire(text)?.let { return "TECH-EXP " + it.groupValues[1] }
            Regex("^Mantenimiento cada (.+) km • Gestionar Sedes$").matchEntire(text)?.let {
                return "Maintenance every " + it.groupValues[1] + " km • Manage Branches"
            }
            Regex("^Mín\\. alerta: (.+)$").matchEntire(text)?.let { return "Min. alert: " + it.groupValues[1] }
            Regex("^Motivo: (.+)$").matchEntire(text)?.let { return "Reason: " + it.groupValues[1] }
            Regex("^ODÓM: (.+)$").matchEntire(text)?.let { return "ODOMETER: " + it.groupValues[1] }
            Regex("^Odómetro: (.+)$").matchEntire(text)?.let { return "Odometer: " + it.groupValues[1] }
            Regex("^Pagado: (.+)$").matchEntire(text)?.let { return "Paid: " + it.groupValues[1] }
            Regex("^Prioridad FIFO #(.+)$").matchEntire(text)?.let { return "FIFO Priority # " + it.groupValues[1] }
            Regex("^Registrado: (.+)$").matchEntire(text)?.let { return "Recorded: " + it.groupValues[1] }
            Regex("^SKU: (.+)$").matchEntire(text)?.let { return "SKU: " + it.groupValues[1] }
            Regex("^Saldo: (.+)$").matchEntire(text)?.let { return "Balance: " + it.groupValues[1] }
            Regex("^Stock actual: (.+) un\\.$").matchEntire(text)?.let { return "Current stock: " + it.groupValues[1] + " units" }
            Regex("^MAPA · (.+) talleres$").matchEntire(text)?.let { return "MAP · " + it.groupValues[1] + " workshops" }
            Regex("^Mantenimiento cada (.+) km • Gestionar Sedes$").matchEntire(text)?.let {
                return "Maintenance every " + it.groupValues[1] + " km • Manage Branches"
            }
            Regex("^Últimas (.+) lecturas recibidas$").matchEntire(text)?.let {
                return "Latest " + it.groupValues[1] + " readings received"
            }
            Regex("^¡Hola, (.+)!$").matchEntire(text)?.let { return "Hi, " + it.groupValues[1] + "!" }
            Regex("^\\((.+) reseñas\\)$").matchEntire(text)?.let { return "(" + it.groupValues[1] + " reviews)" }
            Regex("^\\((.+) reseñas\\)$").matchEntire(text)?.let { return "(" + it.groupValues[1] + " reviews)" }
        } else {
            Regex("^Bay (\\d+)$").matchEntire(text)?.let { return "Bahía " + it.groupValues[1] }
            Regex("^About (.+) km from your location$").matchEntire(text)?.let {
                return "A " + it.groupValues[1] + " km de tu ubicación"
            }
            Regex("^Hi, (.+)\\. Check your vehicles' status here\\.$").matchEntire(text)?.let {
                return "Hola, " + it.groupValues[1] + ". Revisa aquí el estado de tus autos."
            }
            Regex("^Code (.+)$").matchEntire(text)?.let { return "Código " + it.groupValues[1] }
            Regex("^License Plate: (.+)$").matchEntire(text)?.let { return "Placa: " + it.groupValues[1] }
            Regex("^Tax ID: (.+)$").matchEntire(text)?.let { return "RUC: " + it.groupValues[1] }
            Regex("^ID: (.+)$").matchEntire(text)?.let { return "DNI: " + it.groupValues[1] }
            Regex("^Updated (.+)$").matchEntire(text)?.let { return "Actualizado " + it.groupValues[1] }
            Regex("^Canceled \\((.+)\\)$").matchEntire(text)?.let { return "Canceladas (" + it.groupValues[1] + ")" }
            Regex("^Completed \\((.+)\\)$").matchEntire(text)?.let { return "Completadas (" + it.groupValues[1] + ")" }
            Regex("^Pending \\((.+)\\)$").matchEntire(text)?.let { return "Pendientes (" + it.groupValues[1] + ")" }
            Regex("^In progress \\((.+)\\)$").matchEntire(text)?.let { return "En progreso (" + it.groupValues[1] + ")" }
            Regex("^Requests \\((.+)\\)$").matchEntire(text)?.let { return "Solicitudes (" + it.groupValues[1] + ")" }
            Regex("^Active \\((.+)\\)$").matchEntire(text)?.let { return "Activos (" + it.groupValues[1] + ")" }
            Regex("^Status: (.+)$").matchEntire(text)?.let { return "Estado: " + it.groupValues[1] }
            Regex("^Service: (.+)$").matchEntire(text)?.let { return "Servicio: " + it.groupValues[1] }
            Regex("^Reason: (.+)$").matchEntire(text)?.let { return "Motivo: " + it.groupValues[1] }
            Regex("^Location: (.+)$").matchEntire(text)?.let { return "Ubicación: " + it.groupValues[1] }
            Regex("^(.+) Active Bays$").matchEntire(text)?.let { return it.groupValues[1] + " Bahías Activas" }
            Regex("^(.+) active bays$").matchEntire(text)?.let { return it.groupValues[1] + " bahías activas" }
            Regex("^(.+) modules inspected$").matchEntire(text)?.let { return it.groupValues[1] + " módulos inspeccionados" }
            Regex("^(.+) frames in local queue$").matchEntire(text)?.let { return it.groupValues[1] + " tramas en cola local" }
            Regex("^(.+) units$").matchEntire(text)?.let { return it.groupValues[1] + " un." }
            Regex("^(.+) Technicians$").matchEntire(text)?.let { return it.groupValues[1] + " Técnicos" }
            Regex("^\\((.+) reviews\\)$").matchEntire(text)?.let { return "(" + it.groupValues[1] + " reseñas)" }
            Regex("^Document: (.+)$").matchEntire(text)?.let { return "Documento: " + it.groupValues[1] }
            Regex("^Customer: (.+)$").matchEntire(text)?.let { return "Cliente: " + it.groupValues[1] }
            Regex("^Current email: (.+)$").matchEntire(text)?.let { return "Correo actual: " + it.groupValues[1] }
            Regex("^Date: (.+)$").matchEntire(text)?.let { return "Fecha: " + it.groupValues[1] }
            Regex("^Employee: (.+)$").matchEntire(text)?.let { return "Empleado: " + it.groupValues[1] }
            Regex("^Payments: (.+)$").matchEntire(text)?.let { return "Pagos: " + it.groupValues[1] }
            Regex("^Check-in: (.+)$").matchEntire(text)?.let { return "Ingreso: " + it.groupValues[1] }
            Regex("^Started: (.+)$").matchEntire(text)?.let { return "Iniciado: " + it.groupValues[1] }
            Regex("^Initial: (.+) • Available: (.+)$").matchEntire(text)?.let {
                return "Inicial: " + it.groupValues[1] + " • Disponible: " + it.groupValues[2]
            }
            Regex("^Size: (.+) • Format: (.+)$").matchEntire(text)?.let {
                return "Tamaño: " + it.groupValues[1] + " • Formato: " + it.groupValues[2]
            }
            Regex("^History\\s+(.+)$").matchEntire(text)?.let { return "Historial  " + it.groupValues[1] }
            Regex("^Upcoming\\s+(.+)$").matchEntire(text)?.let { return "Próximas  " + it.groupValues[1] }
            Regex("^Health (.+)%$").matchEntire(text)?.let { return "Salud " + it.groupValues[1] + "%" }
            Regex("^Signal (.+)$").matchEntire(text)?.let { return "Señal " + it.groupValues[1] }
            Regex("^Severity: (.+) • Suggested review in (.+) km$").matchEntire(text)?.let {
                return "Severidad: " + it.groupValues[1] + " • Revisión sugerida en " + it.groupValues[2] + " km"
            }
            Regex("^Stock will increase from (.+) to (.+) units\\.$").matchEntire(text)?.let {
                return "Incrementará el stock de " + it.groupValues[1] + " a " + it.groupValues[2] + " unidades."
            }
            Regex("^Code expires in (.+)$").matchEntire(text)?.let { return "El código expira en " + it.groupValues[1] }
            Regex("^We sent a 6-digit code to (.+)\\. Enter it below to verify your identity\\.$").matchEntire(text)?.let {
                return "Hemos enviado un código de 6 dígitos a " + it.groupValues[1] + ". Ingrésalo a continuación para verificar tu identidad."
            }
            Regex("^We sent a 6-digit code to (.+)\\. Enter it below to confirm ownership and create your account\\.$").matchEntire(text)?.let {
                return "Hemos enviado un código de 6 dígitos a " + it.groupValues[1] + ". Ingrésalo a continuación para confirmar tu titularidad y crear tu cuenta."
            }
            Regex("^You are about to cancel appointment (.+) at (.+) scheduled for (.+) at (.+)\\.$").matchEntire(text)?.let {
                return "Estás a punto de cancelar la cita " + it.groupValues[1] + " en " + it.groupValues[2] + " programada para el " + it.groupValues[3] + " a las " + it.groupValues[4] + "."
            }
            Regex("^Your car is being repaired at (.+)$").matchEntire(text)?.let {
                return "Tu auto está en reparación en " + it.groupValues[1]
            }
            Regex("^(.+) evidence file\\(s\\)$").matchEntire(text)?.let {
                return it.groupValues[1] + " archivos de evidencia"
            }
            Regex("^Assigned to (.+)$").matchEntire(text)?.let { return "Asignado a " + it.groupValues[1] }
            Regex("^(.+) Nearby$").matchEntire(text)?.let { return it.groupValues[1] + " Cercanos" }
            Regex("^(.+) active FIFO batches$").matchEntire(text)?.let { return it.groupValues[1] + " lotes FIFO activos" }
            Regex("^(.+) • (.+) active bays$").matchEntire(text)?.let {
                return it.groupValues[1] + " • " + it.groupValues[2] + " Bahías activas"
            }
            Regex("^(.+) · older$").matchEntire(text)?.let { return it.groupValues[1] + " · antigua" }
            Regex("^(.+) · newer$").matchEntire(text)?.let { return it.groupValues[1] + " · reciente" }
            Regex("^(.+) · review in (.+) km$").matchEntire(text)?.let {
                return it.groupValues[1] + " · revisar en " + it.groupValues[2] + " km"
            }
            Regex("^Component to inspect: (.+)$").matchEntire(text)?.let {
                return "Componente a verificar: " + it.groupValues[1]
            }
            Regex("^Code: (.+)$").matchEntire(text)?.let { return "Código: " + it.groupValues[1] }
            Regex("^DTC (.+) · (.+) · (.+)$").matchEntire(text)?.let {
                return "DTC " + it.groupValues[1] + " · " + it.groupValues[2] + " · " + it.groupValues[3]
            }
            Regex("^TECH-EXP (.+)$").matchEntire(text)?.let { return "EXP-TEC " + it.groupValues[1] }
            Regex("^Maintenance every (.+) km • Manage Branches$").matchEntire(text)?.let {
                return "Mantenimiento cada " + it.groupValues[1] + " km • Gestionar Sedes"
            }
            Regex("^Min\\. alert: (.+)$").matchEntire(text)?.let { return "Mín. alerta: " + it.groupValues[1] }
            Regex("^ODOMETER: (.+)$").matchEntire(text)?.let { return "ODÓM: " + it.groupValues[1] }
            Regex("^Odometer: (.+)$").matchEntire(text)?.let { return "Odómetro: " + it.groupValues[1] }
            Regex("^Paid: (.+)$").matchEntire(text)?.let { return "Pagado: " + it.groupValues[1] }
            Regex("^FIFO Priority #(.+)$").matchEntire(text)?.let { return "Prioridad FIFO #" + it.groupValues[1] }
            Regex("^Recorded: (.+)$").matchEntire(text)?.let { return "Registrado: " + it.groupValues[1] }
            Regex("^Balance: (.+)$").matchEntire(text)?.let { return "Saldo: " + it.groupValues[1] }
            Regex("^Current stock: (.+) units$").matchEntire(text)?.let { return "Stock actual: " + it.groupValues[1] + " un." }
            Regex("^MAP · (.+) workshops$").matchEntire(text)?.let { return "MAPA · " + it.groupValues[1] + " talleres" }
            Regex("^Latest (.+) readings received$").matchEntire(text)?.let {
                return "Últimas " + it.groupValues[1] + " lecturas recibidas"
            }
            Regex("^Hi, (.+)!$").matchEntire(text)?.let { return "¡Hola, " + it.groupValues[1] + "!" }
            Regex("^Mileage: (.+) km$").matchEntire(text)?.let { return "Kilometraje: " + it.groupValues[1] + " km" }
            Regex("^Memory used: (.+) KB$").matchEntire(text)?.let { return "Memoria ocupada: " + it.groupValues[1] + " KB" }
            Regex("^Memory used: (.+) KB$").matchEntire(text)?.let { return "Memoria ocupada: " + it.groupValues[1] + " KB" }
            Regex("^Minimum: (.+) · Maximum: (.+) (.+)$").matchEntire(text)?.let {
                return "Mín. " + it.groupValues[1] + " · Máx. " + it.groupValues[2] + " " + it.groupValues[3]
            }
        }
        return null
    }
}
