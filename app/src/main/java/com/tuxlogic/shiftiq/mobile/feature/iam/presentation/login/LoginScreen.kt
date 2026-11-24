package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.scaleOnPress
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.arrowForward
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.googleLogo
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.lockOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.mailOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibility
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibilityOff
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBackgroundLight
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQErrorContainer
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQOnErrorContainer
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQPrimary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate600
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.TelematicsGreen
import com.tuxlogic.shiftiq.mobile.core.model.Role

private val LoginBackground = ShiftIQBackgroundLight
private val LoginHeroStart = Navy900
private val LoginHeroEnd = ShiftIQPrimary
private val LoginFieldBackground = Color.White
private val LoginFieldBorder = ShiftIQPrimary.copy(alpha = 0.38f)

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LoginBackground,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(196.dp)
                    .clip(RoundedCornerShape(bottomStart = 42.dp, bottomEnd = 42.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(LoginHeroStart, LoginHeroEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SI",
                            color = ShiftIQPrimary,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "SHIFT IQ",
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = "Tecnología para una movilidad inteligente",
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = 12.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Bienvenido de vuelta",
                color = Slate900,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Inicia sesión para continuar",
                color = Slate500,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(24.dp))

            val showEmailError =
                (uiState.email.isNotEmpty() && !uiState.isEmailFormatValid) || uiState.emailError != null
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
                placeholder = { Text("Correo electrónico", color = Slate400, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = mailOutline,
                        contentDescription = "Correo electrónico",
                        tint = if (showEmailError) SeverityCrimson else Slate500,
                        modifier = Modifier.size(19.dp)
                    )
                },
                singleLine = true,
                isError = showEmailError,
                supportingText = null,
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Slate900,
                    unfocusedTextColor = Slate900,
                    errorTextColor = Slate900,
                    cursorColor = PrimaryBlue,
                    focusedContainerColor = LoginFieldBackground,
                    unfocusedContainerColor = LoginFieldBackground,
                    errorContainerColor = LoginFieldBackground,
                    focusedBorderColor = ShiftIQPrimary,
                    unfocusedBorderColor = LoginFieldBorder,
                    errorBorderColor = SeverityCrimson
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            if (showEmailError) {
                Text(
                    text = uiState.emailError ?: "Ingresa un correo electrónico válido",
                    color = SeverityCrimson,
                    fontSize = 11.sp,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 3.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            val showPasswordError =
                (uiState.password.isNotEmpty() && uiState.password.length < 8) || uiState.passwordError != null
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                textStyle = LocalTextStyle.current.copy(color = Slate900, fontSize = 14.sp),
                placeholder = { Text("Contraseña", color = Slate400, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = lockOutline,
                        contentDescription = "Contraseña",
                        tint = if (showPasswordError) SeverityCrimson else Slate500,
                        modifier = Modifier.size(19.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityToggle) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) visibilityOff else visibility,
                            contentDescription = if (uiState.isPasswordVisible) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                            tint = Slate500,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                },
                singleLine = true,
                isError = showPasswordError,
                supportingText = null,
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Slate900,
                    unfocusedTextColor = Slate900,
                    errorTextColor = Slate900,
                    cursorColor = PrimaryBlue,
                    focusedContainerColor = LoginFieldBackground,
                    unfocusedContainerColor = LoginFieldBackground,
                    errorContainerColor = LoginFieldBackground,
                    focusedBorderColor = ShiftIQPrimary,
                    unfocusedBorderColor = LoginFieldBorder,
                    errorBorderColor = SeverityCrimson
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onLoginClick() })
            )

            if (showPasswordError) {
                Text(
                    text = uiState.passwordError ?: "La contraseña debe tener al menos 8 caracteres",
                    color = SeverityCrimson,
                    fontSize = 11.sp,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 3.dp)
                )
            } else if (uiState.password.length >= 8) {
                Text(
                    text = "Longitud válida",
                    color = TelematicsGreen,
                    fontSize = 11.sp,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 3.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onRememberMeToggle(!uiState.rememberMe) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.rememberMe,
                        onCheckedChange = onRememberMeToggle,
                        modifier = Modifier.size(34.dp),
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryBlue,
                            uncheckedColor = Slate400,
                            checkmarkColor = Color.White
                        )
                    )
                    Text("Recordarme", color = Slate700, fontSize = 12.sp)
                }
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = ShiftIQPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.clickable(onClick = onForgotPasswordClick)
                )
            }

            if (uiState.errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ShiftIQErrorContainer)
                        .border(1.dp, SeverityCrimson.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = ShiftIQOnErrorContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ShiftIQPrimary)
                    .scaleOnPress(onClick = onLoginClick),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(21.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Iniciar sesión",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = arrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(Modifier.weight(1f), color = Slate200)
                Text(
                    text = "o continúa con",
                    color = Slate500,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(Modifier.weight(1f), color = Slate200)
            }

            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, LoginFieldBorder, CircleShape)
                    .scaleOnPress(onClick = { /* Google sign-in is not configured here. */ }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = googleLogo,
                    contentDescription = "Continuar con Google",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.height(18.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿No tienes una cuenta? ", color = Slate600, fontSize = 12.sp)
                Text(
                    text = "Regístrate",
                    color = ShiftIQPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onNavigateToRegister)
                )
            }
            Spacer(Modifier.height(8.dp))
            }
        }
    }
}

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
