package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.recovery

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthErrorBanner
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthLabeledField
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthPrimaryButton
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthScaffold
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.lockOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.mailOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibility
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibilityOff
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQErrorContainer
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQPrimary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.WalBufferAmber

@Composable
fun RecoveryScreen(
    viewModel: RecoveryViewModel = hiltViewModel(),
    onBackToLogin: () -> Unit,
    onRecoverySuccess: () -> Unit = onBackToLogin
) {
    val state by viewModel.uiState.collectAsState()

    RecoveryContent(
        state = state,
        onBack = { viewModel.onBackClicked(onExitFlow = onBackToLogin) },
        onEmailChanged = viewModel::onEmailChanged,
        onClearEmail = viewModel::onClearEmail,
        onRequestCode = viewModel::onRequestResetCode,
        onOtpCodeChanged = viewModel::onOtpCodeChanged,
        onVerifyOtp = viewModel::onVerifyOtp,
        onResendOtp = viewModel::onResendOtp,
        onNewPasswordChanged = viewModel::onNewPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onToggleNewPasswordVisibility = viewModel::onToggleNewPasswordVisibility,
        onToggleConfirmPasswordVisibility = viewModel::onToggleConfirmPasswordVisibility,
        onResetPassword = { viewModel.onResetPassword(onSuccess = onRecoverySuccess) },
        onNavigateToLogin = onBackToLogin
    )
}

@Composable
fun RecoveryContent(
    state: RecoveryUiState,
    onBack: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onClearEmail: () -> Unit,
    onRequestCode: () -> Unit,
    onOtpCodeChanged: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onResendOtp: () -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onResetPassword: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val topBarTitle = when (state.step) {
        RecoveryStep.EMAIL -> "Recuperar Contraseña"
        RecoveryStep.OTP -> "Validación de Seguridad"
        RecoveryStep.NEW_PASSWORD -> "Crear Nueva Contraseña"
        RecoveryStep.SUCCESS -> "Listo"
    }

    val subtitle = when (state.step) {
        RecoveryStep.EMAIL -> "Te enviaremos un código de verificación a tu correo"
        RecoveryStep.OTP -> "Ingresa el código de 6 dígitos que enviamos"
        RecoveryStep.NEW_PASSWORD -> "Define una contraseña segura para tu cuenta"
        RecoveryStep.SUCCESS -> "Tu contraseña fue actualizada correctamente"
    }

    AuthScaffold(
        title = topBarTitle,
        subtitle = subtitle,
        showBackButton = true,
        showLogo = false,
        onBack = onBack,
        footer = {
            Text(
                text = "Volver al inicio de sesión",
                color = PrimaryBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(onClick = onNavigateToLogin)
            )
        }
    ) {
            if (state.step != RecoveryStep.SUCCESS) {
                RecoveryStepIndicator(currentStep = state.step)
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (state.errorMessage != null) {
                AuthErrorBanner(message = state.errorMessage)
            }

            // Contenido por paso
            when (state.step) {
                RecoveryStep.EMAIL -> {
                    StepEmailContent(
                        email = state.email,
                        emailError = state.emailError,
                        isLoading = state.isLoading,
                        isEmailValid = state.isEmailValid,
                        onEmailChanged = onEmailChanged,
                        onClearEmail = onClearEmail,
                        onRequestCode = onRequestCode
                    )
                }
                RecoveryStep.OTP -> {
                    StepOtpContent(
                        otpCode = state.otpCode,
                        maskedEmail = state.maskedEmail,
                        isLoading = state.isLoading,
                        isOtpValid = state.isOtpValid,
                        timeLeft = state.timeLeft,
                        formattedTime = state.formattedTimeLeft,
                        isResendEnabled = state.isResendEnabled,
                        onOtpChanged = onOtpCodeChanged,
                        onVerify = onVerifyOtp,
                        onResend = onResendOtp
                    )
                }
                RecoveryStep.NEW_PASSWORD -> {
                    StepNewPasswordContent(
                        newPassword = state.newPassword,
                        confirmPassword = state.confirmPassword,
                        newPasswordError = state.newPasswordError,
                        confirmPasswordError = state.confirmPasswordError,
                        isNewPasswordVisible = state.isNewPasswordVisible,
                        isConfirmPasswordVisible = state.isConfirmPasswordVisible,
                        isLoading = state.isLoading,
                        isPasswordLengthValid = state.isPasswordLengthValid,
                        hasUppercase = state.hasUppercase,
                        hasSpecialOrNumber = state.hasSpecialOrNumber,
                        doPasswordsMatch = state.doPasswordsMatch,
                        onNewPasswordChanged = onNewPasswordChanged,
                        onConfirmPasswordChanged = onConfirmPasswordChanged,
                        onToggleNewPasswordVisibility = onToggleNewPasswordVisibility,
                        onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
                        onResetPassword = onResetPassword
                    )
                }
                RecoveryStep.SUCCESS -> {
                    StepSuccessContent(
                        onNavigateToLogin = onNavigateToLogin
                    )
                }
            }
    }
}

@Composable
private fun RecoveryStepIndicator(currentStep: RecoveryStep) {
    val stepIndex = when (currentStep) {
        RecoveryStep.EMAIL -> 1
        RecoveryStep.OTP -> 2
        RecoveryStep.NEW_PASSWORD -> 3
        RecoveryStep.SUCCESS -> 3
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        StepDot(stepNumber = 1, label = "Correo", isActive = stepIndex >= 1, isCurrent = stepIndex == 1)
        StepLine(isActive = stepIndex >= 2)
        StepDot(stepNumber = 2, label = "OTP", isActive = stepIndex >= 2, isCurrent = stepIndex == 2)
        StepLine(isActive = stepIndex >= 3)
        StepDot(stepNumber = 3, label = "Nueva", isActive = stepIndex >= 3, isCurrent = stepIndex == 3)
    }
}

@Composable
private fun StepDot(
    stepNumber: Int,
    label: String,
    isActive: Boolean,
    isCurrent: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isCurrent) PrimaryBlue
                    else if (isActive) TelematicsGreen
                    else Slate200
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isActive && !isCurrent) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = "$stepNumber",
                    color = if (isActive) Color.White else Slate600,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isActive) Slate800 else Slate400,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun StepLine(isActive: Boolean) {
    Box(
        modifier = Modifier
            .width(44.dp)
            .height(2.dp)
            .offset(y = (-10).dp)
            .background(if (isActive) TelematicsGreen else Slate200)
    )
}

// =========================================================================
// PASO 1: INGRESO DE CORREO
// =========================================================================
@Composable
private fun StepEmailContent(
    email: String,
    emailError: String?,
    isLoading: Boolean,
    isEmailValid: Boolean,
    onEmailChanged: (String) -> Unit,
    onClearEmail: () -> Unit,
    onRequestCode: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
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
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Slate900,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa el correo electrónico asociado a tu cuenta y te enviaremos un código de seguridad de 6 dígitos.",
            fontSize = 13.sp,
            color = Slate600,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        val showFormatError = email.isNotBlank() && !isEmailValid
        val isFieldInError = emailError != null || showFormatError
        val fieldErrorMessage = emailError ?: if (showFormatError) "Formato de correo inválido" else null

        AuthLabeledField(
            label = "Correo electrónico",
            value = email,
            onValueChange = onEmailChanged,
            hint = "ejemplo@shiftiq.com",
            leadingIcon = mailOutline,
            isError = isFieldInError,
            errorMessage = fieldErrorMessage,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (!isLoading && isEmailValid) onRequestCode() }
            ),
            trailingIcon = {
                if (email.isNotBlank()) {
                    IconButton(onClick = onClearEmail) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear email",
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthPrimaryButton(
            label = "Enviar código de seguridad",
            isLoading = isLoading,
            enabled = isEmailValid,
            onClick = onRequestCode
        )
    }
}

// =========================================================================
// PASO 2: VALIDACIÓN DE OTP
// =========================================================================
@Composable
private fun StepOtpContent(
    otpCode: String,
    maskedEmail: String,
    isLoading: Boolean,
    isOtpValid: Boolean,
    timeLeft: Int,
    formattedTime: String,
    isResendEnabled: Boolean,
    onOtpChanged: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ShiftIQWarning.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = WalBufferAmber,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Ingresa el Código OTP",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Slate900,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hemos enviado un código de 6 dígitos a $maskedEmail. Ingrésalo a continuación para verificar tu identidad.",
            fontSize = 13.sp,
            color = Slate600,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Contenedor interactivo de 6 casillas OTP
        OtpBoxesView(
            otpValue = otpCode,
            onOtpChanged = onOtpChanged
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Contador regresivo y reenvío
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = null,
                tint = if (timeLeft > 0) Slate500 else WalBufferAmber,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))

            if (timeLeft > 0) {
                Text(
                    text = "El código expira en $formattedTime",
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
                        tint = PrimaryBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Reenviar código",
                        color = PrimaryBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        AuthPrimaryButton(
            label = "Verificar código",
            isLoading = isLoading,
            enabled = isOtpValid,
            onClick = onVerify
        )
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
                                color = if (isFocused) PrimaryBlue else if (char.isNotEmpty()) Slate400 else Slate200,
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

// =========================================================================
// PASO 3: NUEVA CONTRASEÑA
// =========================================================================
@Composable
private fun StepNewPasswordContent(
    newPassword: String,
    confirmPassword: String,
    newPasswordError: String?,
    confirmPasswordError: String?,
    isNewPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    isLoading: Boolean,
    isPasswordLengthValid: Boolean,
    hasUppercase: Boolean,
    hasSpecialOrNumber: Boolean,
    doPasswordsMatch: Boolean,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onResetPassword: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ShiftIQSuccess.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = TelematicsGreen,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Crea tu Nueva Contraseña",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Slate900,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Asegúrate de cumplir con los requisitos de seguridad para proteger tu cuenta de ShiftIQ.",
            fontSize = 13.sp,
            color = Slate600,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthLabeledField(
            label = "Nueva contraseña",
            value = newPassword,
            onValueChange = onNewPasswordChanged,
            hint = "Mínimo 8 caracteres",
            leadingIcon = lockOutline,
            isError = newPasswordError != null,
            errorMessage = newPasswordError,
            visualTransformation = if (isNewPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                IconButton(onClick = onToggleNewPasswordVisibility) {
                    Icon(
                        imageVector = if (isNewPasswordVisible) visibilityOff else visibility,
                        contentDescription = if (isNewPasswordVisible) "Hide password" else "Show password",
                        tint = Slate500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        AuthLabeledField(
            label = "Confirmar nueva contraseña",
            value = confirmPassword,
            onValueChange = onConfirmPasswordChanged,
            hint = "Repite tu nueva contraseña",
            leadingIcon = lockOutline,
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError,
            visualTransformation = if (isConfirmPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                IconButton(onClick = onToggleConfirmPasswordVisibility) {
                    Icon(
                        imageVector = if (isConfirmPasswordVisible) visibilityOff else visibility,
                        contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password",
                        tint = Slate500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Checklist visual de requisitos
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100),
            border = BorderStroke(1.dp, Slate200)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Requisitos de seguridad:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordRequirementRow(label = "Mínimo 8 caracteres", isMet = isPasswordLengthValid)
                PasswordRequirementRow(label = "Al menos una letra mayúscula", isMet = hasUppercase)
                PasswordRequirementRow(label = "Al menos un número o símbolo especial", isMet = hasSpecialOrNumber)
                PasswordRequirementRow(label = "Las contraseñas coinciden", isMet = doPasswordsMatch)
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        val isFormValid = isPasswordLengthValid && hasUppercase && hasSpecialOrNumber && doPasswordsMatch

        AuthPrimaryButton(
            label = "Actualizar contraseña",
            isLoading = isLoading,
            enabled = isFormValid,
            onClick = onResetPassword
        )
    }
}

@Composable
private fun PasswordRequirementRow(label: String, isMet: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(if (isMet) TelematicsGreen else Slate200),
            contentAlignment = Alignment.Center
        ) {
            if (isMet) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isMet) Slate800 else Slate500,
            fontWeight = if (isMet) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// =========================================================================
// PASO 4: CONFIRMACIÓN EXITOSA
// =========================================================================
@Composable
private fun StepSuccessContent(
    onNavigateToLogin: () -> Unit
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
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Contraseña Actualizada!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Slate900,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Tu contraseña ha sido restablecida exitosamente. Ahora puedes iniciar sesión en ShiftIQ con tus nuevas credenciales.",
            fontSize = 14.sp,
            color = Slate600,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthPrimaryButton(
            label = "Iniciar sesión",
            isLoading = false,
            onClick = onNavigateToLogin
        )
    }
}
