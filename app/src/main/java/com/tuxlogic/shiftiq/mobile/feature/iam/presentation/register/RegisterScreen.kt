package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.register

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthErrorBanner
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthLabeledField
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthPrimaryButton
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthScaffold
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.scaleOnPress
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.arrowBack
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.arrowForward
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.badgeIcon
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.bluetooth
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.checkCircle
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.directionsCar
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.errorOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.lockOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.mailOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.person
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.photoCamera
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.smartphone
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.syncIcon
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibility
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibilityOff
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.NavActivePill
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Primary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQAccentSkyBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQErrorContainer
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQPrimary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate600
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate800
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.TelematicsGreen
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.WalBufferAmber

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onBackToLogin: () -> Unit,
    onNavigateToRecovery: () -> Unit = {},
    onRegistrationSuccess: () -> Unit = onBackToLogin
) {
    val state by viewModel.uiState.collectAsState()
    val blePermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }
    val blePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.all { it }) {
            viewModel.onScanBleDevices()
        }
    }

    RegisterContent(
        state = state,
        onBack = { viewModel.onPreviousStep(onExit = onBackToLogin) },
        onNextStep = viewModel::onNextStep,
        onPreviousStep = { viewModel.onPreviousStep(onExit = onBackToLogin) },
        onCorrectData = viewModel::onCorrectData,
        onSubmit = { viewModel.onSubmitRegistration(onSuccess = onRegistrationSuccess) },
        onNavigateToLogin = onBackToLogin,
        onNavigateToRecovery = onNavigateToRecovery,
        onRegistrationSuccess = onRegistrationSuccess,
        // Step 1
        onFullNameChange = viewModel::onFullNameChange,
        onDocumentNumberChange = viewModel::onDocumentNumberChange,
        onPhoneChange = viewModel::onPhoneChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onToggleConfirmPasswordVisibility = viewModel::onToggleConfirmPasswordVisibility,
        onToggleAcceptsNotifications = viewModel::onToggleAcceptsNotifications,
        onToggleAcceptsTerms = viewModel::onToggleAcceptsTerms,
        // Step 2
        onBrandChange = viewModel::onBrandChange,
        onModelChange = viewModel::onModelChange,
        onYearChange = viewModel::onYearChange,
        onPlateNumberChange = viewModel::onPlateNumberChange,
        onVinChange = viewModel::onVinChange,
        onScanBleDevices = { blePermissionLauncher.launch(blePermissions) },
        onSelectDongle = viewModel::onSelectDongle,
        // Step OTP
        onOtpCodeChanged = viewModel::onOtpCodeChanged,
        onVerifyOtp = { viewModel.onVerifyOtpAndRegister(onSuccess = onRegistrationSuccess) },
        onResendOtp = viewModel::onResendOtp
    )
}

@Composable
fun RegisterContent(
    state: RegisterUiState,
    onBack: () -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onCorrectData: () -> Unit,
    onSubmit: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onRegistrationSuccess: () -> Unit = onNavigateToLogin,
    onFullNameChange: (String) -> Unit,
    onDocumentNumberChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onToggleAcceptsNotifications: (Boolean) -> Unit,
    onToggleAcceptsTerms: (Boolean) -> Unit,
    onBrandChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onPlateNumberChange: (String) -> Unit,
    onVinChange: (String) -> Unit,
    onScanBleDevices: () -> Unit,
    onSelectDongle: (String) -> Unit,
    onOtpCodeChanged: (String) -> Unit = {},
    onVerifyOtp: () -> Unit = {},
    onResendOtp: () -> Unit = {}
) {
    val topBarTitle = when (state.step) {
        RegisterStep.STEP_1_USER -> "Crear cuenta"
        RegisterStep.STEP_2_VEHICLE -> "Vehículo y Dongle"
        RegisterStep.STEP_3_CONFIRM -> "Confirmación Técnica"
        RegisterStep.OTP -> "Verificación de Correo"
        RegisterStep.SUCCESS -> "Registro Exitoso"
        RegisterStep.ERROR -> "Crear cuenta"
    }
    val subtitle = when (state.step) {
        RegisterStep.STEP_1_USER -> "Registra tus datos para acceder al portal cliente"
        RegisterStep.STEP_2_VEHICLE -> "Vincula tu vehículo y el dongle OBD-II"
        RegisterStep.STEP_3_CONFIRM -> "Revisa la información antes de continuar"
        RegisterStep.OTP -> "Ingresa el código de 6 dígitos enviado a tu correo"
        RegisterStep.SUCCESS -> "Tu cuenta quedó lista para iniciar sesión"
        RegisterStep.ERROR -> "Corrige los datos o recupera el acceso existente"
    }

    AuthScaffold(
        title = topBarTitle,
        subtitle = subtitle,
        showBackButton = true,
        showLogo = false,
        onBack = onBack,
        footer = {
            if (state.step == RegisterStep.STEP_1_USER) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿Ya tienes cuenta? ", color = SoftSlate, fontSize = 13.sp)
                    Text(
                        text = "Inicia sesión",
                        color = ShiftIQPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onNavigateToLogin)
                    )
                }
            }
        }
    ) {
                if (state.step in listOf(RegisterStep.STEP_1_USER, RegisterStep.STEP_2_VEHICLE, RegisterStep.STEP_3_CONFIRM)) {
                    WizardStepper(
                        currentStep = when (state.step) {
                            RegisterStep.STEP_1_USER -> 1
                            RegisterStep.STEP_2_VEHICLE -> 2
                            else -> 3
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                when (state.step) {
                    RegisterStep.STEP_1_USER -> {
                        Step1OwnerContent(
                            state = state,
                            onFullNameChange = onFullNameChange,
                            onDocumentNumberChange = onDocumentNumberChange,
                            onPhoneChange = onPhoneChange,
                            onEmailChange = onEmailChange,
                            onPasswordChange = onPasswordChange,
                            onConfirmPasswordChange = onConfirmPasswordChange,
                            onTogglePasswordVisibility = onTogglePasswordVisibility,
                            onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
                            onToggleAcceptsNotifications = onToggleAcceptsNotifications,
                            onToggleAcceptsTerms = onToggleAcceptsTerms,
                            onNext = onNextStep,
                            onNavigateToLogin = onNavigateToLogin
                        )
                    }

                    RegisterStep.STEP_2_VEHICLE -> {
                        Step2VehicleContent(
                            state = state,
                            onBrandChange = onBrandChange,
                            onModelChange = onModelChange,
                            onYearChange = onYearChange,
                            onPlateNumberChange = onPlateNumberChange,
                            onVinChange = onVinChange,
                            onScanBleDevices = onScanBleDevices,
                            onSelectDongle = onSelectDongle,
                            onBack = onPreviousStep,
                            onNext = onNextStep
                        )
                    }

                    RegisterStep.STEP_3_CONFIRM -> {
                        Step3ConfirmContent(
                            state = state,
                            onBack = onPreviousStep,
                            onSubmit = onSubmit
                        )
                    }

                    RegisterStep.ERROR -> {
                        StepErrorContent(
                            errorMessage = state.errorMessage ?: "El correo electrónico o DNI ingresado ya está registrado en ShiftIQ.",
                            onCorrectData = onCorrectData,
                            onNavigateToRecovery = onNavigateToRecovery
                        )
                    }

                    RegisterStep.SUCCESS -> {
                        StepSuccessContent(
                            onGoToDashboard = onRegistrationSuccess
                        )
                    }

                    RegisterStep.OTP -> {
                        StepOtpContent(
                            state = state,
                            onOtpChanged = onOtpCodeChanged,
                            onVerify = onVerifyOtp,
                            onResend = onResendOtp,
                            onBack = onPreviousStep
                        )
                    }
                }
    }
}

private val SoftSlate = Slate500

// Registration progress tabs. Stages are intentionally informational; the existing
// Back and Continue actions remain the only controls that advance the registration flow.
@Composable
private fun WizardStepper(currentStep: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepItem(1, "Propietario", currentStep >= 1, currentStep == 1)
            HorizontalDivider(
                modifier = Modifier.weight(1f).padding(horizontal = 7.dp),
                color = if (currentStep > 1) ShiftIQPrimary else Slate200,
                thickness = 2.dp
            )
            StepItem(2, "Vehículo", currentStep >= 2, currentStep == 2)
            HorizontalDivider(
                modifier = Modifier.weight(1f).padding(horizontal = 7.dp),
                color = if (currentStep > 2) ShiftIQPrimary else Slate200,
                thickness = 2.dp
            )
            StepItem(3, "Confirmar", currentStep >= 3, currentStep == 3)
        }
    }
}

@Composable
private fun StepItem(
    stepNumber: Int,
    title: String,
    isActive: Boolean,
    isCurrent: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isActive) ShiftIQPrimary else Color.White)
                .border(
                    width = if (isCurrent) 2.dp else 1.dp,
                    color = if (isActive) ShiftIQPrimary else Slate400,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isActive && !isCurrent) "✓" else stepNumber.toString(),
                color = if (isActive) Color.White else Slate500,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = title,
            color = if (isCurrent) ShiftIQPrimary else Slate500,
            fontSize = 10.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

// =========================================================================
// PASO 1: Datos del Propietario
// =========================================================================
@Composable
private fun Step1OwnerContent(
    state: RegisterUiState,
    onFullNameChange: (String) -> Unit,
    onDocumentNumberChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onToggleAcceptsNotifications: (Boolean) -> Unit,
    onToggleAcceptsTerms: (Boolean) -> Unit,
    onNext: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(4.dp).height(20.dp).background(ShiftIQPrimary, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Paso 1: Datos del Propietario",
            color = Slate900,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = "Ingresa tus datos personales para asociar la titularidad del escáner y recibir reportes técnicos de telemetría.",
        color = Slate600,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )

    Spacer(modifier = Modifier.height(14.dp))

    val showNameError = state.fullNameError != null
    AuthLabeledField(
        label = "Nombre Completo *",
        value = state.fullName,
        onValueChange = onFullNameChange,
        hint = "Rodrigo Alarcón Flores",
        leadingIcon = person,
        isError = showNameError,
        errorMessage = state.fullNameError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
    )

    Spacer(modifier = Modifier.height(10.dp))

    val showDniError = (state.documentNumber.isNotEmpty() && !state.isDniValid) || state.documentError != null
    AuthLabeledField(
        label = "Documento de Identidad (DNI) *",
        value = state.documentNumber,
        onValueChange = onDocumentNumberChange,
        hint = "47891234",
        leadingIcon = badgeIcon,
        isError = showDniError,
        errorMessage = state.documentError
            ?: "El DNI debe tener 8 dígitos numéricos (${state.documentNumber.length}/8)",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
    )
    if (!showDniError) {
        Spacer(modifier = Modifier.height(4.dp))
        val dniHelper = when {
            state.isLookingUpDni -> "Consultando en RENIEC..."
            state.dniVerified -> "✓ DNI verificado en RENIEC"
            state.isDniValid -> "✓ DNI válido"
            else -> "8 dígitos numéricos"
        }
        val dniHelperColor = when {
            state.dniVerified || state.isDniValid -> TelematicsGreen
            else -> Slate500
        }
        Text(
            text = dniHelper,
            color = dniHelperColor,
            fontSize = 11.sp,
            fontWeight = if (state.dniVerified || state.isDniValid) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(start = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    val showPhoneError = (state.phone.isNotEmpty() && !state.isPhoneValid) || state.phoneError != null
    AuthLabeledField(
        label = "Teléfono Celular WhatsApp *",
        value = state.phone,
        onValueChange = onPhoneChange,
        hint = "987654321",
        leadingIcon = smartphone,
        isError = showPhoneError,
        errorMessage = state.phoneError
            ?: "El celular debe tener 9 dígitos (${state.phone.length}/9)",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
    )
    if (!showPhoneError) {
        Spacer(modifier = Modifier.height(4.dp))
        val phoneHelper = if (state.isPhoneValid) {
            "✓ Celular válido para alertas (+51)"
        } else {
            "9 dígitos sin código de país (+51)"
        }
        Text(
            text = phoneHelper,
            color = if (state.isPhoneValid) TelematicsGreen else Slate500,
            fontSize = 11.sp,
            fontWeight = if (state.isPhoneValid) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(start = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    val showEmailError = (state.email.isNotEmpty() && !state.isEmailValid) || state.emailError != null
    AuthLabeledField(
        label = "Correo Electrónico *",
        value = state.email,
        onValueChange = onEmailChange,
        hint = "usuario@ejemplo.com",
        leadingIcon = mailOutline,
        isError = showEmailError,
        errorMessage = state.emailError ?: "Ingresa un correo electrónico válido",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
    )
    if (!showEmailError && state.isEmailValid) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "✓ Correo válido",
            color = TelematicsGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    val showPasswordError = (state.password.isNotEmpty() && !state.isPasswordValid) || state.passwordError != null
    AuthLabeledField(
        label = "Contraseña *",
        value = state.password,
        onValueChange = onPasswordChange,
        hint = "Mínimo 8 caracteres",
        leadingIcon = lockOutline,
        isError = showPasswordError,
        errorMessage = run {
            if (state.passwordError != null) {
                state.passwordError
            } else if (showPasswordError) {
                val missing = mutableListOf<String>()
                if (!state.isPasswordLengthValid) missing.add("8+ caracteres")
                if (!state.hasUppercase) missing.add("1 mayúscula")
                if (!state.hasNumber) missing.add("1 número")
                "Falta: ${missing.joinToString(", ")}"
            } else {
                null
            }
        },
        visualTransformation = if (state.isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    imageVector = if (state.isPasswordVisible) visibilityOff else visibility,
                    contentDescription = null,
                    tint = Slate500,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
    if (!showPasswordError) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (state.isPasswordValid) {
                "✓ Contraseña segura"
            } else {
                "Mínimo 8 caracteres, 1 mayúscula y 1 número"
            },
            color = if (state.isPasswordValid) TelematicsGreen else Slate500,
            fontSize = 11.sp,
            fontWeight = if (state.isPasswordValid) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(start = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    val showConfirmError = (state.confirmPassword.isNotEmpty() && !state.doPasswordsMatch) || state.confirmPasswordError != null
    AuthLabeledField(
        label = "Confirmar Contraseña *",
        value = state.confirmPassword,
        onValueChange = onConfirmPasswordChange,
        hint = "Repite tu contraseña",
        leadingIcon = syncIcon,
        isError = showConfirmError,
        errorMessage = state.confirmPasswordError ?: "Las contraseñas no coinciden",
        visualTransformation = if (state.isConfirmPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        trailingIcon = {
            IconButton(onClick = onToggleConfirmPasswordVisibility) {
                Icon(
                    imageVector = if (state.isConfirmPasswordVisible) visibilityOff else visibility,
                    contentDescription = null,
                    tint = Slate500,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
    if (!showConfirmError && state.doPasswordsMatch) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "✓ Las contraseñas coinciden",
            color = TelematicsGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Checkboxes de Notificaciones y Términos (Material 3 con alto contraste y desmarcados por defecto)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggleAcceptsNotifications(!state.notificationsAccepted) }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = state.notificationsAccepted,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = ShiftIQPrimary,
                uncheckedColor = Slate400,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Acepto recibir notificaciones automáticas de fallas (DTC) y telemetría diagnóstica vía WhatsApp.",
            color = Slate700,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggleAcceptsTerms(!state.termsAccepted) }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = state.termsAccepted,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = ShiftIQPrimary,
                uncheckedColor = Slate400,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Acepto los Términos de Servicio y el Tratamiento de Datos Telemáticos del Vehículo.",
            color = Slate700,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }

    if (state.fullName.isNotBlank() && state.isDniValid && state.isPhoneValid && state.isEmailValid && state.isPasswordValid && state.doPasswordsMatch && (!state.termsAccepted || !state.notificationsAccepted)) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = ShiftIQPrimary.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, ShiftIQAccentSkyBlue.copy(alpha = 0.55f))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = errorOutline, contentDescription = null, tint = ShiftIQPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (!state.termsAccepted && !state.notificationsAccepted) {
                        "Marca ambas casillas (Términos y Notificaciones) para habilitar el botón."
                    } else if (!state.termsAccepted) {
                        "Debes aceptar los Términos de Servicio para continuar."
                    } else {
                        "Debes aceptar las Notificaciones de telemetría para continuar."
                    },
                    color = ShiftIQPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (!state.errorMessage.isNullOrBlank()) {
        AuthErrorBanner(message = state.errorMessage)
    }

    Spacer(modifier = Modifier.height(20.dp))

    val isStep1Ready = state.isStep1Valid
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isStep1Ready) Primary else Slate200)
            .scaleOnPress(onClick = onNext),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Continuar a Datos del Vehículo",
                color = if (isStep1Ready) Color.White else Slate500,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = arrowForward,
                contentDescription = null,
                tint = if (isStep1Ready) Color.White else Slate500,
                modifier = Modifier.size(16.dp).offset(x = 2.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = "¿Ya tienes una cuenta? ", color = Slate600, fontSize = 13.sp)
        Text(
            text = "Iniciar Sesión",
            color = Primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onNavigateToLogin)
        )
    }
}

// =========================================================================
// PASO 2: Especificaciones y Dongle IoT
// =========================================================================
@Composable
private fun Step2VehicleContent(
    state: RegisterUiState,
    onBrandChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onPlateNumberChange: (String) -> Unit,
    onVinChange: (String) -> Unit,
    onScanBleDevices: () -> Unit,
    onSelectDongle: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Text(
        text = "Paso 2: Especificaciones y Dongle IoT",
        color = Slate900,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = "Configura el perfil de tu auto y sincroniza el escáner Bluetooth OBD-II.",
        color = Slate600,
        fontSize = 13.sp
    )

    Spacer(modifier = Modifier.height(14.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Slate50)
            .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = photoCamera, contentDescription = null, tint = Slate400, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Subir fotografía frontal o lateral (Opcional)", color = Slate600, fontSize = 12.sp)
            Text(text = "[ Explorar archivo ]", color = ShiftIQPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        AuthLabeledField(
            label = "Marca *",
            value = state.brand,
            onValueChange = onBrandChange,
            hint = "Toyota",
            leadingIcon = directionsCar,
            modifier = Modifier.weight(1f)
        )
        AuthLabeledField(
            label = "Modelo *",
            value = state.model,
            onValueChange = onModelChange,
            hint = "Corolla",
            leadingIcon = directionsCar,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        AuthLabeledField(
            label = "Año de Fab. *",
            value = state.year,
            onValueChange = onYearChange,
            hint = "2020",
            leadingIcon = Icons.Filled.AccessTime,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
        AuthLabeledField(
            label = "Placa Vehicular *",
            value = state.plateNumber,
            onValueChange = onPlateNumberChange,
            hint = "ABC-123",
            leadingIcon = directionsCar,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    AuthLabeledField(
        label = "Número de Chasis (VIN) *",
        value = state.vin,
        onValueChange = onVinChange,
        hint = "3KPA24AB1KE129841",
        leadingIcon = badgeIcon,
        trailingIcon = {
            if (state.isVinValid) {
                Icon(
                    imageVector = checkCircle,
                    contentDescription = null,
                    tint = TelematicsGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "17 caracteres. Ubicado en la tarjeta de propiedad o base del parabrisas.",
        color = Slate500,
        fontSize = 11.sp,
        modifier = Modifier.padding(start = 4.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Card Dongle IoT
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate50),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(ShiftIQPrimary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = bluetooth, contentDescription = null, tint = ShiftIQPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "Enlace de Dispositivo OBD-II", color = Slate900, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Enciende el contacto del vehículo para activar.", color = Slate500, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(10.dp))
                    .scaleOnPress(onClick = onScanBleDevices),
                contentAlignment = Alignment.Center
            ) {
                if (state.isScanningBle) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ShiftIQPrimary, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Buscando señal BLE...", color = Slate700, fontSize = 12.sp)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = syncIcon, contentDescription = null, tint = Slate700, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Escanear Dispositivos Cercanos", color = Slate700, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (state.hasPairedDongle) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ShiftIQSuccess.copy(alpha = 0.10f))
                        .border(BorderStroke(1.dp, ShiftIQSuccess.copy(alpha = 0.35f)), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = checkCircle, contentDescription = null, tint = TelematicsGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = state.selectedDongleId, color = Slate900, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text(text = "Señal ${state.dongleSignal}", color = Slate500, fontSize = 11.sp)
                    }
                    if (state.dongleBattery > 0) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ShiftIQSuccess.copy(alpha = 0.22f)).padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "Listo", color = TelematicsGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Opcional: puedes continuar sin dongle y vincularlo después desde el garaje.",
                    color = Slate500,
                    fontSize = 11.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(22.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(14.dp))
                .scaleOnPress(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Atrás", color = Slate700, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Box(
            modifier = Modifier
                .weight(2f)
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (state.isStep2Valid) Primary else Primary.copy(alpha = 0.6f))
                .scaleOnPress(onClick = onNext),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Vincular y Continuar", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = arrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// =========================================================================
// PASO 3: Confirmación y Resumen Técnico
// =========================================================================
@Composable
private fun Step3ConfirmContent(
    state: RegisterUiState,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(ShiftIQPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = checkCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "(( • ))", color = ShiftIQPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "¡Vehículo y Telemetría Listos!", color = Slate900, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Revisa los detalles técnicos antes de crear tu cuenta ShiftIQ.", color = Slate600, fontSize = 13.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ShiftIQPrimary.copy(alpha = 0.08f))
                .border(BorderStroke(1.dp, NavActivePill), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(TelematicsGreen))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Vínculo OBD-II BLE 5.2 Activo", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(text = "LAT: 12ms", color = Slate500, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Slate50),
            border = BorderStroke(1.dp, Slate200)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "RESUMEN DE REGISTRO", color = Slate700, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Text(text = "#REG-2024-DRV", color = ShiftIQPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color.White).padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = directionsCar, contentDescription = null, tint = ShiftIQPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "${state.brand} ${state.model} ${state.year}", color = Slate900, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                SummaryRow(label = "Propietario:", value = state.fullName)
                SummaryRow(label = "DNI:", value = state.documentNumber)
                SummaryRow(label = "Correo:", value = state.email)
                SummaryRow(label = "Celular:", value = "+51 ${state.phone}")
                SummaryRow(label = "Placa:", value = state.plateNumber)
                SummaryRow(label = "VIN:", value = state.vin)
                SummaryRow(label = "Dispositivo IoT:", value = state.selectedDongleId)
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        AuthPrimaryButton(
            label = "Crear Cuenta y Finalizar",
            isLoading = state.isLoading,
            onClick = onSubmit
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(14.dp))
                .scaleOnPress(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Editar Datos Anteriores", color = Slate700, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Slate500, fontSize = 12.sp)
        Text(text = value, color = Slate900, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

// =========================================================================
// ESTADO ERROR
// =========================================================================
@Composable
private fun StepErrorContent(
    errorMessage: String,
    onCorrectData: () -> Unit,
    onNavigateToRecovery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(ShiftIQErrorContainer)
                .border(BorderStroke(1.5.dp, SeverityCrimson.copy(alpha = 0.45f)), RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = errorOutline, contentDescription = null, tint = SeverityCrimson, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "No se pudo crear la cuenta",
            color = Slate900,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = errorMessage,
            color = Slate600,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(26.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(14.dp))
                .scaleOnPress(onClick = onNavigateToRecovery),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Recuperar Contraseña", color = Slate800, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ShiftIQPrimary)
                .scaleOnPress(onClick = onCorrectData),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Corregir Datos", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "¿Necesitas ayuda? Contacta a soporte",
            color = Slate500,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

// =========================================================================
// ESTADO SUCCESS
// =========================================================================
@Composable
private fun StepSuccessContent(
    onGoToDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(ShiftIQSuccess),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = checkCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Cuenta y Vehículo Registrados!",
            color = Slate900,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Bienvenido a ShiftIQ. Tu vehículo y protocolo de telemetría han sido vinculados correctamente. Ya puedes iniciar sesión con tu cuenta.",
            color = Slate600,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ShiftIQPrimary)
                .scaleOnPress(onClick = onGoToDashboard),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Ir al Inicio de Sesión", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = arrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// =========================================================================
// PASO OTP: Verificación de Código de Correo
// =========================================================================
@Composable
private fun StepOtpContent(
    state: RegisterUiState,
    onOtpChanged: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ShiftIQPrimary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = ShiftIQPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Verifica tu Correo Electrónico",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Slate900,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hemos enviado un código de 6 dígitos a ${state.email}. Ingrésalo a continuación para confirmar tu titularidad y crear tu cuenta.",
            fontSize = 13.sp,
            color = Slate600,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        OtpBoxesView(
            otpValue = state.otpCode,
            onOtpChanged = onOtpChanged
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = null,
                tint = if (state.timeLeft > 0) Slate500 else WalBufferAmber,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))

            if (state.timeLeft > 0) {
                Text(
                    text = "El código expira en ${state.formattedTimeLeft}",
                    fontSize = 12.sp,
                    color = Slate600,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = "¿No recibiste el código?",
                    fontSize = 12.sp,
                    color = Slate600
                )
                Spacer(modifier = Modifier.width(6.dp))
                Row(
                    modifier = Modifier.clickable(onClick = onResend),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = ShiftIQPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Reenviar código",
                        color = ShiftIQPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (!state.errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(14.dp))
            AuthErrorBanner(message = state.errorMessage)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (state.isOtpCodeComplete && !state.isLoading) Primary else Slate200)
                .scaleOnPress(
                    onClick = {
                        if (state.isOtpCodeComplete && !state.isLoading) onVerify()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Verificar y Crear Cuenta",
                        color = if (state.isOtpCodeComplete) Color.White else Slate500,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = arrowForward,
                        contentDescription = null,
                        tint = if (state.isOtpCodeComplete) Color.White else Slate500,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(14.dp))
                .scaleOnPress(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Volver a Datos de Confirmación", color = Slate700, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun OtpBoxesView(
    otpValue: String,
    onOtpChanged: (String) -> Unit
) {
    BasicTextField(
        value = otpValue,
        onValueChange = onOtpChanged,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                for (index in 0 until 6) {
                    val char = otpValue.getOrNull(index)?.toString() ?: ""
                    val isFocused = otpValue.length == index

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) ShiftIQPrimary else if (char.isNotEmpty()) Slate400 else Slate200,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    ShiftIQTheme {
        RegisterContent(
            state = RegisterUiState(
                fullName = "Rodrigo Alarcón Flores",
                documentNumber = "47891234",
                phone = "987654321",
                email = "rodrigo.alarcon@email.com"
            ),
            onBack = {},
            onNextStep = {},
            onPreviousStep = {},
            onCorrectData = {},
            onSubmit = {},
            onNavigateToLogin = {},
            onNavigateToRecovery = {},
            onFullNameChange = {},
            onDocumentNumberChange = {},
            onPhoneChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onToggleAcceptsNotifications = {},
            onToggleAcceptsTerms = {},
            onBrandChange = {},
            onModelChange = {},
            onYearChange = {},
            onPlateNumberChange = {},
            onVinChange = {},
            onScanBleDevices = {},
            onSelectDongle = {}
        )
    }
}
