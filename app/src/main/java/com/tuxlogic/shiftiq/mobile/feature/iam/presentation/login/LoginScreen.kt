package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthErrorBanner
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthLabeledField
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthPrimaryButton
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.AuthScaffold
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.lockOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.mailOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibility
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibilityOff
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQPrimary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.model.Role

/**
 * Login screen layout mirrored from Flutter [LoginPage] + [AuthScaffold].
 */
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onLoginSuccess: (Role) -> Unit,
    onNavigateToRegister: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onPasswordVisibilityToggle: () -> Unit = {},
    onRememberMeToggle: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LaunchedEffect(uiState.isSuccess, uiState.userRole) {
        if (uiState.isSuccess && uiState.userRole != null) {
            onLoginSuccess(uiState.userRole)
        }
    }

    val showEmailError =
        (uiState.email.isNotEmpty() && !uiState.isEmailFormatValid) || uiState.emailError != null
    val showPasswordError =
        (uiState.password.isNotEmpty() && uiState.password.length < 8) || uiState.passwordError != null

    AuthScaffold(
        title = "Bienvenido de vuelta",
        subtitle = "Inicia sesión para continuar con tu portal ShiftIQ",
        modifier = modifier,
        footer = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿No tienes una cuenta?",
                        color = SoftMuted,
                        fontSize = 13.sp
                    )
                    Text(
                        text = " Crear cuenta",
                        color = ShiftIQPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onNavigateToRegister)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Clientes · Personal · Talleros",
                    color = SoftHint,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    ) {
        if (uiState.errorMessage != null) {
            AuthErrorBanner(message = uiState.errorMessage)
        }

        AuthLabeledField(
            label = "Correo electrónico",
            value = uiState.email,
            onValueChange = onEmailChanged,
            hint = "tu@correo.com",
            leadingIcon = mailOutline,
            isError = showEmailError,
            errorMessage = uiState.emailError ?: "Ingresa un correo electrónico válido",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        AuthLabeledField(
            label = "Contraseña",
            value = uiState.password,
            onValueChange = onPasswordChanged,
            hint = "Tu contraseña",
            leadingIcon = lockOutline,
            isError = showPasswordError,
            errorMessage = uiState.passwordError ?: "La contraseña debe tener al menos 8 caracteres",
            visualTransformation = if (uiState.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onLoginClick() }),
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) visibilityOff else visibility,
                        contentDescription = if (uiState.isPasswordVisible) {
                            "Ocultar contraseña"
                        } else {
                            "Mostrar contraseña"
                        },
                        tint = SoftMuted
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = ShiftIQPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onForgotPasswordClick)
        )

        Spacer(modifier = Modifier.height(12.dp))

        AuthPrimaryButton(
            label = "Iniciar sesión",
            isLoading = uiState.isLoading,
            onClick = onLoginClick
        )
    }
}

private val SoftMuted = Slate500
private val SoftHint = Slate400

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    ShiftIQTheme {
        LoginScreen(
            uiState = LoginUiState(email = "usuario@empresa.com"),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {},
            onLoginSuccess = {}
        )
    }
}
