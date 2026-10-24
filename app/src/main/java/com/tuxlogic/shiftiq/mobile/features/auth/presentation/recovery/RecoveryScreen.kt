package com.tuxlogic.shiftiq.mobile.features.auth.presentation.recovery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.checkCircle
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.closeIcon
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.errorOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.hourglassEmpty
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.lockOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.mailOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.shieldIcon
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.syncIcon
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibility
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibilityOff
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Primary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
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
fun RecoveryScreen(
    viewModel: RecoveryViewModel = hiltViewModel(),
    onBackToLogin: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    RecoveryContent(
        state = state,
        onBack = { viewModel.onBackClicked(onExitFlow = onBackToLogin) },
        onEmailChange = viewModel::onEmailChanged,
        onClearEmail = viewModel::onClearEmail,
        onRequestCode = viewModel::onRequestResetCode,
        onOtpChange = viewModel::onOtpCodeChanged,
        onVerifyOtp = viewModel::onVerifyOtp,
        onResendOtp = viewModel::onResendOtp,
        onNewPasswordChange = viewModel::onNewPasswordChanged,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChanged,
        onToggleNewPasswordVisibility = viewModel::onToggleNewPasswordVisibility,
        onToggleConfirmPasswordVisibility = viewModel::onToggleConfirmPasswordVisibility,
        onResetPassword = { viewModel.onResetPassword() },
        onBackToLogin = onBackToLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryContent(
    state: RecoveryUiState,
    onBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onClearEmail: () -> Unit,
    onRequestCode: () -> Unit,
    onOtpChange: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onResendOtp: () -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onResetPassword: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val title = when (state.step) {
        RecoveryStep.EMAIL -> "Identificar Cuenta"
        RecoveryStep.OTP -> "Enter Security Code"
        RecoveryStep.NEW_PASSWORD -> "Create New Password"
        RecoveryStep.SUCCESS -> "Contraseña Actualizada"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
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
                    .padding(horizontal = 22.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                when (state.step) {
                    RecoveryStep.EMAIL -> {
                        Step1EmailContent(
                            state = state,
                            onEmailChange = onEmailChange,
                            onClearEmail = onClearEmail,
                            onRequestCode = onRequestCode,
                            onCancel = onBackToLogin
                        )
                    }
                    RecoveryStep.OTP -> {
                        Step2OtpContent(
                            state = state,
                            onOtpChange = onOtpChange,
                            onVerifyOtp = onVerifyOtp,
                            onResendOtp = onResendOtp
                        )
                    }
                    RecoveryStep.NEW_PASSWORD -> {
                        Step3NewPasswordContent(
                            state = state,
                            onNewPasswordChange = onNewPasswordChange,
                            onConfirmPasswordChange = onConfirmPasswordChange,
                            onToggleNewPasswordVisibility = onToggleNewPasswordVisibility,
                            onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
                            onResetPassword = onResetPassword
                        )
                    }
                    RecoveryStep.SUCCESS -> {
                        Step4SuccessContent(
                            onBackToLogin = onBackToLogin
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// PASO 1: Identificación (EMAIL) - Mockup mk-mod1-15
// =========================================================================
@Composable
private fun Step1EmailContent(
    state: RecoveryUiState,
    onEmailChange: (String) -> Unit,
    onClearEmail: () -> Unit,
    onRequestCode: () -> Unit,
    onCancel: () -> Unit
) {
    // 1. Badge Pill
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFEFF6FF))
            .border(BorderStroke(1.dp, Color(0xFFDBEAFE)), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(PrimaryBlue)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Paso 1 de 3 : Identificación",
            color = PrimaryBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Heading & Subtitle
    Text(
        text = "¿Olvidaste tu contraseña?",
        color = Slate900,
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 34.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Ingresa el correo electrónico asociado a tu cuenta (dueño de taller o conductor). Te enviaremos un código de seguridad de 6 dígitos válido por 60 minutos.",
        color = Slate600,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    // 3. Form Card (Concentric 20.dp)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Slate50),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Correo Electrónico Registrado",
                    color = Slate800,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Requerido",
                    color = Slate400,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    color = Slate900,
                    fontSize = 14.sp
                ),
                placeholder = {
                    Text(
                        text = "ejemplo@taller.pe",
                        color = Slate400,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = mailOutline,
                        contentDescription = "Correo",
                        tint = Slate500,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (state.email.isNotEmpty()) {
                        IconButton(onClick = onClearEmail) {
                            Icon(
                                imageVector = closeIcon,
                                contentDescription = "Limpiar correo",
                                tint = Slate400,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                isError = state.emailError != null,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Slate900,
                    unfocusedTextColor = Slate900,
                    errorTextColor = Slate900,
                    cursorColor = PrimaryBlue,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Slate200,
                    errorBorderColor = Color(0xFFEF4444)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onRequestCode() })
            )

            if (state.emailError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.emailError,
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Helper note
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = accessTime,
                    contentDescription = null,
                    tint = Slate400,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Asegúrate de tener acceso activo a esta bandeja de entrada.",
                    color = Slate500,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Callout Box: Validación de Perfil Activo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = shieldIcon,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Validación de Perfil Activo",
                        color = Slate900,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Detectaremos automáticamente si tu acceso es como Taller Mecánico o Conductor Asignado.",
                        color = Slate500,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }

    // Inline Error Banner
    if (state.errorMessage != null) {
        Spacer(modifier = Modifier.height(12.dp))
        ErrorBanner(message = state.errorMessage)
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Primary CTA: Enviar Código de Seguridad ->
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Primary)
            .scaleOnPress(
                enabled = !state.isLoading,
                onClick = onRequestCode
            ),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Enviar Código de Seguridad",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = arrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = 2.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Secondary CTA: Cancelar y volver al inicio de sesión
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(14.dp))
            .scaleOnPress(onClick = onCancel),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Cancelar y volver al inicio de sesión",
            color = Slate700,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// =========================================================================
// PASO 2: Validación OTP (6 dígitos) - Mockup mk-mod1-16
// =========================================================================
@Composable
private fun Step2OtpContent(
    state: RecoveryUiState,
    onOtpChange: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onResendOtp: () -> Unit
) {
    // 1. Badge Pill
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFEFF6FF))
            .border(BorderStroke(1.dp, Color(0xFFDBEAFE)), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(PrimaryBlue)
        )
        Spacer(modifier = Modifier.width(6.dp))
         Text(
            text = "Paso 2 de 3: Validación",
            color = PrimaryBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Heading & Subtitle with masked email
    Text(
        text = "Revisa tu correo",
        color = Slate900,
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 34.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Hemos enviado un código de 6 dígitos a ${state.maskedEmail}. Ingresa el código recibido para continuar.",
        color = Slate600,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(24.dp))

    // 3. 6 OTP Input Boxes
    OtpInputBoxes(
        otpValue = state.otpCode,
        onOtpChange = onOtpChange
    )

    Spacer(modifier = Modifier.height(22.dp))

    // 4. Timer & Resend Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Slate50),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = accessTime,
                    contentDescription = null,
                    tint = Slate700,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "El código caduca en 59:12 min",
                    color = Slate800,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No recibiste el correo?",
                    color = Slate500,
                    fontSize = 13.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = hourglassEmpty,
                        contentDescription = null,
                        tint = Slate700,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (state.isResendEnabled) "Disponible" else "Reenviar en ${state.formattedTimeLeft}",
                        color = Slate800,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Resend button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (state.isResendEnabled) Color.White else Slate100)
                    .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(12.dp))
                    .scaleOnPress(
                        enabled = state.isResendEnabled && !state.isLoading,
                        onClick = onResendOtp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = mailOutline,
                        contentDescription = null,
                        tint = if (state.isResendEnabled) Slate800 else Slate400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reenviar nuevo código",
                        color = if (state.isResendEnabled) Slate800 else Slate400,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Validation error banner
    if (state.errorMessage != null) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFFEF2F2))
                .border(BorderStroke(1.dp, Color(0xFFFCA5A5)), RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = errorOutline,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Estado de Validación",
                    color = Color(0xFF991B1B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = state.errorMessage,
                    color = Color(0xFFB91C1C),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Primary CTA: Verificar Código ->
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (state.otpCode.length == 6) Primary else Primary.copy(alpha = 0.6f))
            .scaleOnPress(
                enabled = state.otpCode.length == 6 && !state.isLoading,
                onClick = onVerifyOtp
            ),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Verificar Código",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = arrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = 2.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Footer Support link & Security Badge
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Problemas para acceder? Contactar a soporte de taller",
            color = Slate700,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = shieldIcon,
                contentDescription = null,
                tint = Slate500,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ShiftIQ Secure Authentication",
                color = Slate500,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// =========================================================================
// PASO 3: Nueva Contraseña - Mockup mk-mod1-17
// =========================================================================
@Composable
private fun Step3NewPasswordContent(
    state: RecoveryUiState,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onResetPassword: () -> Unit
) {
    // 1. Badge Pill
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFEFF6FF))
            .border(BorderStroke(1.dp, Color(0xFFDBEAFE)), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "≡ PASO 3 DE 3: RESTABLECIMIENTO",
            color = PrimaryBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Headings
    Text(
        text = "Crea tu nueva contraseña",
        color = Slate900,
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 34.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Tu contraseña anterior será revocada inmediatamente de todos los dispositivos.",
        color = Slate600,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    // 3. Credential Form Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Slate50),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nueva Contraseña header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nueva Contraseña *",
                    color = Slate800,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Obligatorio",
                    color = Slate400,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.newPassword,
                onValueChange = onNewPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    color = Slate900,
                    fontSize = 14.sp
                ),
                placeholder = {
                    Text(text = "ShiftSafe2024!", color = Slate400, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = lockOutline,
                        contentDescription = "Contraseña",
                        tint = Slate500,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onToggleNewPasswordVisibility) {
                        Icon(
                            imageVector = if (state.isNewPasswordVisible) visibilityOff else visibility,
                            contentDescription = "Visibilidad",
                            tint = Slate500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                singleLine = true,
                isError = state.newPasswordError != null,
                visualTransformation = if (state.isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Slate900,
                    unfocusedTextColor = Slate900,
                    errorTextColor = Slate900,
                    cursorColor = PrimaryBlue,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Slate200,
                    errorBorderColor = Color(0xFFEF4444)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            if (state.newPasswordError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.newPasswordError,
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmar Contraseña header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Confirmar Nueva Contraseña *",
                    color = Slate800,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (state.doPasswordsMatch) {
                    Text(
                        text = "✓ Coincide",
                        color = TelematicsGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    color = Slate900,
                    fontSize = 14.sp
                ),
                placeholder = {
                    Text(text = "ShiftSafe2024!", color = Slate400, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = syncIcon,
                        contentDescription = "Confirmar",
                        tint = Slate500,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (state.doPasswordsMatch) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = checkCircle,
                                contentDescription = "Coincide",
                                tint = TelematicsGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        IconButton(onClick = onToggleConfirmPasswordVisibility) {
                            Icon(
                                imageVector = if (state.isConfirmPasswordVisible) visibilityOff else visibility,
                                contentDescription = "Visibilidad",
                                tint = Slate500,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                isError = state.confirmPasswordError != null,
                visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Slate900,
                    unfocusedTextColor = Slate900,
                    errorTextColor = Slate900,
                    cursorColor = PrimaryBlue,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Slate200,
                    errorBorderColor = Color(0xFFEF4444)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onResetPassword() })
            )

            if (state.confirmPasswordError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.confirmPasswordError,
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security Requirements Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = shieldIcon,
                        contentDescription = null,
                        tint = Slate700,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REQUISITOS DE SEGURIDAD DE CUENTA:",
                        color = Slate800,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                RequirementItem(
                    isMet = state.isPasswordLengthValid,
                    label = "Mínimo 8 caracteres alfanuméricos"
                )
                Spacer(modifier = Modifier.height(6.dp))
                RequirementItem(
                    isMet = state.hasUppercase,
                    label = "Al menos una letra mayúscula"
                )
                Spacer(modifier = Modifier.height(6.dp))
                RequirementItem(
                    isMet = state.hasSpecialOrNumber,
                    label = "Al menos un número o símbolo especial"
                )
            }
        }
    }

    if (state.errorMessage != null) {
        Spacer(modifier = Modifier.height(14.dp))
        ErrorBanner(message = state.errorMessage)
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Primary CTA: Guardar Contraseña ->
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (state.isNewPasswordValid && state.doPasswordsMatch) Primary else Primary.copy(alpha = 0.6f))
            .scaleOnPress(
                enabled = state.isNewPasswordValid && state.doPasswordsMatch && !state.isLoading,
                onClick = onResetPassword
            ),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Guardar Contraseña",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = arrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = 2.dp)
                )
            }
        }
    }
}

// =========================================================================
// PASO 4: Éxito Modal/Card - Mockup mk-mod1-18
// =========================================================================
@Composable
private fun Step4SuccessContent(
    onBackToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Green Success Icon Card
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF22C55E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = checkCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Contraseña Actualizada!",
            color = Slate900,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Tu acceso ha sido restablecido con éxito. Ya puedes iniciar sesión con tus nuevas credenciales.",
            color = Slate600,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Primary)
                .scaleOnPress(onClick = onBackToLogin),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ir al Inicio de Sesión (Login)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = arrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = 2.dp)
                )
            }
        }
    }
}

// =========================================================================
// Componentes Auxiliares
// =========================================================================

@Composable
private fun OtpInputBoxes(
    otpValue: String,
    onOtpChange: (String) -> Unit
) {
    BasicTextField(
        value = otpValue,
        onValueChange = onOtpChange,
        textStyle = LocalTextStyle.current.copy(
            color = Slate900,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(6) { index ->
                    val char = otpValue.getOrNull(index)?.toString() ?: ""
                    val isFocused = otpValue.length == index || (index == 5 && otpValue.length == 6)

                    Box(
                        modifier = Modifier
                            .width(46.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) PrimaryBlue else Slate200,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun RequirementItem(
    isMet: Boolean,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = checkCircle,
            contentDescription = null,
            tint = if (isMet) TelematicsGreen else Slate400,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = if (isMet) Slate800 else Slate500,
            fontSize = 12.sp,
            fontWeight = if (isMet) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFEF2F2))
            .border(BorderStroke(1.dp, Color(0xFFFCA5A5)), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = message,
            color = Color(0xFFB91C1C),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecoveryScreenPreview() {
    ShiftIqTheme {
        RecoveryContent(
            state = RecoveryUiState(
                step = RecoveryStep.OTP,
                email = "adm***@tallersilva.com",
                otpCode = "481"
            ),
            onBack = {},
            onEmailChange = {},
            onClearEmail = {},
            onRequestCode = {},
            onOtpChange = {},
            onVerifyOtp = {},
            onResendOtp = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onToggleNewPasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onResetPassword = {},
            onBackToLogin = {}
        )
    }
}
