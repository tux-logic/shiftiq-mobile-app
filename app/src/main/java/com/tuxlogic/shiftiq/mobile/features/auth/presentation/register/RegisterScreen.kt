package com.tuxlogic.shiftiq.mobile.features.auth.presentation.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.component.scaleOnPress
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.accessTime
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.shieldIcon
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.smartphone
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.syncIcon
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibility
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibilityOff
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Primary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIqTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate600
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate800
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.TelematicsGreen

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onBackToLogin: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onRegistrationSuccess: () -> Unit = onBackToLogin
) {
    val state by viewModel.uiState.collectAsState()

    RegisterContent(
        state = state,
        onBack = { viewModel.onPreviousStep(onExit = onBackToLogin) },
        onNextStep = viewModel::onNextStep,
        onPreviousStep = { viewModel.onPreviousStep(onExit = onBackToLogin) },
        onCorrectData = viewModel::onCorrectData,
        onSubmit = { viewModel.onSubmitRegistration(onSuccess = {}) },
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
        onScanBleDevices = viewModel::onScanBleDevices,
        onSelectDongle = viewModel::onSelectDongle
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
    onSelectDongle: (String) -> Unit
) {
    val topBarTitle = when (state.step) {
        RegisterStep.STEP_1_USER -> "Registration Step 1"
        RegisterStep.STEP_2_VEHICLE -> "Registration Step 2"
        RegisterStep.STEP_3_CONFIRM -> "Registration Step 3"
        RegisterStep.OTP -> "Verificar Cuenta"
        RegisterStep.SUCCESS -> "Registro Exitoso"
        RegisterStep.ERROR -> "Registro de Conductor"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        color = Slate900,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = arrowBack,
                            contentDescription = "Atrás",
                            tint = Slate900,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Stepper visible en los primeros 3 pasos
                if (state.step in listOf(RegisterStep.STEP_1_USER, RegisterStep.STEP_2_VEHICLE, RegisterStep.STEP_3_CONFIRM)) {
                    WizardStepper(
                        currentStep = when (state.step) {
                            RegisterStep.STEP_1_USER -> 1
                            RegisterStep.STEP_2_VEHICLE -> 2
                            else -> 3
                        }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
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
                        // Reutilización directa para validación OTP si backend lo requiere
                        StepSuccessContent(
                            onGoToDashboard = onRegistrationSuccess
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// WIZARD STEPPER COMPONENT
// =========================================================================
@Composable
private fun WizardStepper(currentStep: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate50),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StepItem(stepNumber = 1, title = "Propietario", isActive = currentStep >= 1, isCurrent = currentStep == 1)
            HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = if (currentStep > 1) PrimaryBlue else Slate200, thickness = 2.dp)
            StepItem(stepNumber = 2, title = "Vehículo", isActive = currentStep >= 2, isCurrent = currentStep == 2)
            HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = if (currentStep > 2) PrimaryBlue else Slate200, thickness = 2.dp)
            StepItem(stepNumber = 3, title = "Confirmar", isActive = currentStep >= 3, isCurrent = currentStep == 3)
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
                .size(30.dp)
                .clip(CircleShape)
                .background(if (isActive) PrimaryBlue else Color.White)
                .border(BorderStroke(if (isCurrent) 2.dp else 1.dp, if (isActive) PrimaryBlue else Slate400), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber.toString(),
                color = if (isActive) Color.White else Slate500,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = if (isActive) PrimaryBlue else Slate500,
            fontSize = 11.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// =========================================================================
// PASO 1: Datos del Propietario (mk-mod1-03 + mk-mod1-07)
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
    // Header
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(4.dp).height(20.dp).background(PrimaryBlue, RoundedCornerShape(2.dp)))
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

    // Callout box OBD-II
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEFF6FF))
            .border(BorderStroke(1.dp, Color(0xFFDBEAFE)), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = badgeIcon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = "Asociación de Protocolo OBD-II", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = "Los datos coincidirán con la tarjeta de propiedad", color = Slate600, fontSize = 11.sp)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Form inputs
    Text(text = "Nombre Completo *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(4.dp))
    val showNameError = state.fullNameError != null
    OutlinedTextField(
        value = state.fullName,
        onValueChange = onFullNameChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
        placeholder = { Text("Rodrigo Alarcón Flores", color = Slate400, fontSize = 14.sp) },
        leadingIcon = { Icon(imageVector = person, contentDescription = null, tint = if (showNameError) SeverityCrimson else Slate500, modifier = Modifier.size(20.dp)) },
        singleLine = true,
        isError = showNameError,
        supportingText = {
            state.fullNameError?.let { errorText ->
                Text(text = errorText, color = SeverityCrimson, fontSize = 11.sp)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Slate900, unfocusedTextColor = Slate900, errorTextColor = Slate900, cursorColor = PrimaryBlue,
            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Slate200,
            errorBorderColor = SeverityCrimson, errorContainerColor = Color.White
        )
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Documento de Identidad (DNI)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Documento de Identidad (DNI) *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(text = "8 dígitos", color = Slate500, fontSize = 11.sp)
    }
    Spacer(modifier = Modifier.height(4.dp))
    val showDniError = (state.documentNumber.isNotEmpty() && !state.isDniValid) || state.documentError != null
    OutlinedTextField(
        value = state.documentNumber,
        onValueChange = onDocumentNumberChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
        placeholder = { Text("47891234", color = Slate400, fontSize = 14.sp) },
        leadingIcon = { Icon(imageVector = badgeIcon, contentDescription = null, tint = if (showDniError) SeverityCrimson else Slate500, modifier = Modifier.size(20.dp)) },
        singleLine = true,
        isError = showDniError,
        supportingText = {
            if (showDniError) {
                Text(
                    text = state.documentError ?: "El DNI debe tener 8 dígitos numéricos (${state.documentNumber.length}/8)",
                    color = SeverityCrimson,
                    fontSize = 11.sp
                )
            } else if (state.isLookingUpDni) {
                Text(text = "Consultando en RENIEC...", color = Slate500, fontSize = 11.sp)
            } else if (state.dniVerified) {
                Text(text = "✓ DNI verificado en RENIEC", color = TelematicsGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            } else if (state.isDniValid) {
                Text(text = "✓ DNI válido", color = TelematicsGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            } else {
                Text(text = "8 dígitos numéricos", color = Slate500, fontSize = 11.sp)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Slate900, unfocusedTextColor = Slate900, errorTextColor = Slate900, cursorColor = PrimaryBlue,
            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Slate200,
            errorBorderColor = SeverityCrimson, errorContainerColor = Color.White
        )
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Teléfono Celular WhatsApp
    Text(text = "Teléfono Celular WhatsApp *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(4.dp))
    val showPhoneError = (state.phone.isNotEmpty() && !state.isPhoneValid) || state.phoneError != null
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Slate100)
                .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "+51", color = Slate800, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = state.phone,
            onValueChange = onPhoneChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
            placeholder = { Text("987654321", color = Slate400, fontSize = 14.sp) },
            leadingIcon = { Icon(imageVector = smartphone, contentDescription = null, tint = if (showPhoneError) SeverityCrimson else Slate500, modifier = Modifier.size(20.dp)) },
            singleLine = true,
            isError = showPhoneError,
            supportingText = {
                if (showPhoneError) {
                    Text(
                        text = state.phoneError ?: "El celular debe tener 9 dígitos (${state.phone.length}/9)",
                        color = SeverityCrimson,
                        fontSize = 11.sp
                    )
                } else if (state.isPhoneValid) {
                    Text(text = "✓ Celular válido para alertas", color = TelematicsGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Text(text = "9 dígitos sin código de país", color = Slate500, fontSize = 11.sp)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Slate900, unfocusedTextColor = Slate900, errorTextColor = Slate900, cursorColor = PrimaryBlue,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Slate200,
                errorBorderColor = SeverityCrimson, errorContainerColor = Color.White
            )
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Correo Electrónico
    Text(text = "Correo Electrónico *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(4.dp))
    val showEmailError = (state.email.isNotEmpty() && !state.isEmailValid) || state.emailError != null
    OutlinedTextField(
        value = state.email,
        onValueChange = onEmailChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
        placeholder = { Text("usuario@ejemplo.com", color = Slate400, fontSize = 14.sp) },
        leadingIcon = { Icon(imageVector = mailOutline, contentDescription = null, tint = if (showEmailError) SeverityCrimson else Slate500, modifier = Modifier.size(20.dp)) },
        singleLine = true,
        isError = showEmailError,
        supportingText = {
            if (showEmailError) {
                Text(
                    text = state.emailError ?: "Ingresa un correo electrónico válido",
                    color = SeverityCrimson,
                    fontSize = 11.sp
                )
            } else if (state.isEmailValid) {
                Text(text = "✓ Correo válido", color = TelematicsGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Slate900, unfocusedTextColor = Slate900, errorTextColor = Slate900, cursorColor = PrimaryBlue,
            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Slate200,
            errorBorderColor = SeverityCrimson, errorContainerColor = Color.White
        )
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Contraseña
    Text(text = "Contraseña *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(4.dp))
    val showPasswordError = (state.password.isNotEmpty() && !state.isPasswordValid) || state.passwordError != null
    OutlinedTextField(
        value = state.password,
        onValueChange = onPasswordChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
        placeholder = { Text("Mínimo 8 caracteres", color = Slate400, fontSize = 14.sp) },
        leadingIcon = { Icon(imageVector = lockOutline, contentDescription = null, tint = if (showPasswordError) SeverityCrimson else Slate500, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(imageVector = if (state.isPasswordVisible) visibilityOff else visibility, contentDescription = null, tint = Slate500, modifier = Modifier.size(20.dp))
            }
        },
        singleLine = true,
        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = showPasswordError,
        supportingText = {
            if (showPasswordError) {
                val missing = mutableListOf<String>()
                if (!state.isPasswordLengthValid) missing.add("8+ caracteres")
                if (!state.hasUppercase) missing.add("1 mayúscula")
                if (!state.hasNumber) missing.add("1 número")
                Text(
                    text = state.passwordError ?: "Falta: ${missing.joinToString(", ")}",
                    color = SeverityCrimson,
                    fontSize = 11.sp
                )
            } else if (state.isPasswordValid) {
                Text(text = "✓ Contraseña segura", color = TelematicsGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            } else {
                Text(text = "Mínimo 8 caracteres, 1 mayúscula y 1 número", color = Slate500, fontSize = 11.sp)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Slate900, unfocusedTextColor = Slate900, errorTextColor = Slate900, cursorColor = PrimaryBlue,
            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Slate200,
            errorBorderColor = SeverityCrimson, errorContainerColor = Color.White
        )
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Confirmar Contraseña
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Confirmar Contraseña *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        if (state.doPasswordsMatch) {
            Text(text = "✓ Coincide", color = TelematicsGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
    val showConfirmError = (state.confirmPassword.isNotEmpty() && !state.doPasswordsMatch) || state.confirmPasswordError != null
    OutlinedTextField(
        value = state.confirmPassword,
        onValueChange = onConfirmPasswordChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
        placeholder = { Text("Repite tu contraseña", color = Slate400, fontSize = 14.sp) },
        leadingIcon = { Icon(imageVector = syncIcon, contentDescription = null, tint = if (showConfirmError) SeverityCrimson else Slate500, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            IconButton(onClick = onToggleConfirmPasswordVisibility) {
                Icon(imageVector = if (state.isConfirmPasswordVisible) visibilityOff else visibility, contentDescription = null, tint = Slate500, modifier = Modifier.size(20.dp))
            }
        },
        singleLine = true,
        visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = showConfirmError,
        supportingText = {
            if (showConfirmError) {
                Text(
                    text = state.confirmPasswordError ?: "Las contraseñas no coinciden",
                    color = SeverityCrimson,
                    fontSize = 11.sp
                )
            } else if (state.doPasswordsMatch) {
                Text(text = "✓ Las contraseñas coinciden", color = TelematicsGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Slate900, unfocusedTextColor = Slate900, errorTextColor = Slate900, cursorColor = PrimaryBlue,
            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Slate200,
            errorBorderColor = SeverityCrimson, errorContainerColor = Color.White
        )
    )

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
                checkedColor = PrimaryBlue,
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
                checkedColor = PrimaryBlue,
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

    // Feedback visual si faltan marcar los checkboxes
    if (state.fullName.isNotBlank() && state.isDniValid && state.isPhoneValid && state.isEmailValid && state.isPasswordValid && state.doPasswordsMatch && (!state.termsAccepted || !state.notificationsAccepted)) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = errorOutline, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (!state.termsAccepted && !state.notificationsAccepted) {
                        "Marca ambas casillas (Términos y Notificaciones) para habilitar el botón."
                    } else if (!state.termsAccepted) {
                        "Debes aceptar los Términos de Servicio para continuar."
                    } else {
                        "Debes aceptar las Notificaciones de telemetría para continuar."
                    },
                    color = PrimaryBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // Mensaje de error general si aplica
    if (!state.errorMessage.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
            border = BorderStroke(1.dp, Color(0xFFFECACA))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = errorOutline, contentDescription = null, tint = SeverityCrimson, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = state.errorMessage, color = Color(0xFFB91C1C), fontSize = 12.sp)
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Security badge card
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Slate50)
            .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = shieldIcon, contentDescription = null, tint = TelematicsGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Cifrado de Telemetría", color = Slate900, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = "Conexión ISO 15765-4 CAN encriptada", color = Slate500, fontSize = 11.sp)
        }
        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color.White).border(BorderStroke(1.dp, Slate200), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text(text = "v4.8", color = Slate700, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // CTA Button
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
        Text(text = "Iniciar Sesión", color = Primary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateToLogin))
    }
}

// =========================================================================
// PASO 2: Especificaciones y Dongle IoT (mk-mod1-04)
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

    // Upload box placeholder
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
            Text(text = "[ Explorar archivo ]", color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Marca y Modelo en Row
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Marca *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = state.brand,
                onValueChange = onBrandChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Slate900, unfocusedTextColor = Slate900, cursorColor = PrimaryBlue)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Modelo *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = state.model,
                onValueChange = onModelChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Slate900, unfocusedTextColor = Slate900, cursorColor = PrimaryBlue)
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Año y Placa
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Año de Fab. *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = state.year,
                onValueChange = onYearChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Slate900, unfocusedTextColor = Slate900, cursorColor = PrimaryBlue)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Placa Vehicular *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = state.plateNumber,
                onValueChange = onPlateNumberChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
                placeholder = { Text("ABC-123", color = Slate400, fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Slate900, unfocusedTextColor = Slate900, cursorColor = PrimaryBlue)
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // VIN Chasis
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Número de Chasis (VIN) *", color = Slate800, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(text = "17 caracteres", color = Slate500, fontSize = 11.sp)
    }
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = state.vin,
        onValueChange = onVinChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
        placeholder = { Text("3KPA24AB1KE129841", color = Slate400, fontSize = 14.sp) },
        trailingIcon = {
            if (state.isVinValid) {
                Icon(imageVector = checkCircle, contentDescription = null, tint = TelematicsGreen, modifier = Modifier.size(18.dp))
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Slate900, unfocusedTextColor = Slate900, cursorColor = PrimaryBlue)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = "Ubicado en la tarjeta de propiedad o base del parabrisas.", color = Slate500, fontSize = 11.sp)

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
                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = bluetooth, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "Enlace de Dispositivo OBD-II", color = Slate900, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Enciende el contacto del vehículo para activar.", color = Slate500, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón Escanear
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(10.dp))
                    .scaleOnPress(enabled = !state.isScanningBle, onClick = onScanBleDevices),
                contentAlignment = Alignment.Center
            ) {
                if (state.isScanningBle) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PrimaryBlue, strokeWidth = 2.dp)
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

            Spacer(modifier = Modifier.height(10.dp))

            // Dispositivo encontrado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF0FDF4))
                    .border(BorderStroke(1.dp, Color(0xFFBBF7D0)), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = checkCircle, contentDescription = null, tint = TelematicsGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = state.selectedDongleId, color = Slate900, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(text = "Señal ${state.dongleSignal}", color = Slate500, fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFDCFCE7)).padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "${state.dongleBattery}%", color = TelematicsGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(22.dp))

    // Botones de acción
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
                .scaleOnPress(enabled = state.isStep2Valid, onClick = onNext),
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
// PASO 3: Confirmación y Resumen Técnico (mk-mod1-05)
// =========================================================================
@Composable
private fun Step3ConfirmContent(
    state: RegisterUiState,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Icono superior
        Box(
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = checkCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "(( • ))", color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "¡Vehículo y Telemetría Listos!", color = Slate900, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Revisa los detalles técnicos antes de crear tu cuenta ShiftIQ.", color = Slate600, fontSize = 13.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(14.dp))

        // Latency pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFEFF6FF))
                .border(BorderStroke(1.dp, Color(0xFFDBEAFE)), RoundedCornerShape(10.dp))
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

        // Resumen técnico
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Slate50),
            border = BorderStroke(1.dp, Slate200)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "RESUMEN DE REGISTRO", color = Slate700, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Text(text = "#REG-2024-DRV", color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Auto badge
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color.White).padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = directionsCar, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
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

        // CTA Final
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Primary)
                .scaleOnPress(enabled = !state.isLoading, onClick = onSubmit),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Crear Cuenta y Finalizar", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = arrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

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
// ESTADO ERROR (mk-mod1-08)
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
        // Exclamation icon card
        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFFEF2F2))
                .border(BorderStroke(1.5.dp, Color(0xFFFCA5A5)), RoundedCornerShape(22.dp)),
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

        // Botón 1: Recuperar Contraseña (Card blanca con borde)
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

        // Botón 2: Corregir Datos (Azul principal)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Primary)
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
                .background(Color(0xFF22C55E)),
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
                .background(Primary)
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    ShiftIqTheme {
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
