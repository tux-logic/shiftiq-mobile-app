package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.scaleOnPress
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.arrowForward
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.checkCircle
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.googleLogo
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.appleLogo
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.lockOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.mailOutline
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibility
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.visibilityOff
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Primary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
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
import com.tuxlogic.shiftiq.mobile.core.model.Role

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
    var contentVisible by remember { mutableStateOf(false) }
    val waveTransition = rememberInfiniteTransition(label = "login-waves")
    val wavePhase by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "login-wave-phase"
    )
    LaunchedEffect(Unit) {
        delay(80)
        contentVisible = true
    }

    LaunchedEffect(uiState.isSuccess, uiState.userRole) {
        if (uiState.isSuccess && uiState.userRole != null) {
            onLoginSuccess(uiState.userRole)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Slate50,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Brand hero follows the reference composition and keeps a dedicated logo area.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(252.dp)
                    .background(
                        Brush.linearGradient(listOf(PrimaryBlue, Color(0xFF1748B8))),
                        LoginHeroShape
                    )
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(LoginHeroShape)
                ) {
                    val w = size.width
                    val h = size.height
                    val drift = wavePhase * w * 0.16f
                    val backWave = Path().apply {
                        moveTo(0f, h * 0.66f)
                        cubicTo(w * 0.24f + drift, h * 0.82f, w * 0.42f + drift, h * 0.53f, w * 0.68f, h * 0.70f)
                        cubicTo(w * 0.84f, h * 0.81f, w * 0.93f, h * 0.72f, w, h * 0.62f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    val frontWave = Path().apply {
                        moveTo(0f, h * 0.76f)
                        cubicTo(w * 0.20f, h * 0.62f, w * 0.43f + drift, h * 0.90f, w * 0.66f, h * 0.79f)
                        cubicTo(w * 0.82f, h * 0.71f, w * 0.92f, h * 0.84f, w, h * 0.74f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(backWave, Color.White.copy(alpha = 0.075f), style = Fill)
                    drawPath(frontWave, Color.White.copy(alpha = 0.06f), style = Fill)
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.TopStart).padding(start = 8.dp, top = 4.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center).padding(top = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f))
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("LOGO", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Bienvenido", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Inicia sesión para continuar", color = Color.White.copy(alpha = 0.88f), fontSize = 14.sp)
                }
            }

            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(450)) + slideInVertically(
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) { it / 7 }
            ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp)) {
            Spacer(modifier = Modifier.height(8.dp))

            // Credential Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate200),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header Correo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Correo electrónico",
                                color = Slate800,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = " *",
                                color = Color(0xFFEF4444),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (uiState.isEmailFormatValid) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Verificado",
                                    color = TelematicsGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = checkCircle,
                                    contentDescription = null,
                                    tint = TelematicsGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val showEmailError = (uiState.email.isNotEmpty() && !uiState.isEmailFormatValid) || uiState.emailError != null
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = onEmailChanged,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            color = Slate900,
                            fontSize = 14.sp
                        ),
                        placeholder = {
                            Text(
                                text = "usuario@empresa.com",
                                color = Slate400,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = mailOutline,
                                contentDescription = "Email",
                                tint = if (showEmailError) SeverityCrimson else Slate500,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        isError = showEmailError,
                        supportingText = {
                            if (showEmailError) {
                                Text(
                                    text = uiState.emailError ?: "Ingresa un correo electrónico con formato válido",
                                    color = SeverityCrimson,
                                    fontSize = 11.sp
                                )
                            } else if (uiState.isEmailFormatValid) {
                                Text(
                                    text = "✓ Correo válido",
                                    color = TelematicsGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        },
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
                            errorBorderColor = SeverityCrimson
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Header Contraseña
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Contraseña",
                                color = Slate800,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = " *",
                                color = SeverityCrimson,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val showPasswordError = (uiState.password.isNotEmpty() && uiState.password.length < 8) || uiState.passwordError != null
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            color = Slate900,
                            fontSize = 14.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Ingresa tu contraseña",
                                color = Slate400,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = lockOutline,
                                contentDescription = "Password",
                                tint = if (showPasswordError) SeverityCrimson else Slate500,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = onPasswordVisibilityToggle) {
                                Icon(
                                    imageVector = if (uiState.isPasswordVisible) visibilityOff else visibility,
                                    contentDescription = if (uiState.isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = Slate500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        singleLine = true,
                        isError = showPasswordError,
                        supportingText = {
                            if (showPasswordError) {
                                Text(
                                    text = uiState.passwordError ?: "La contraseña debe tener al menos 8 caracteres (${uiState.password.length}/8)",
                                    color = SeverityCrimson,
                                    fontSize = 11.sp
                                )
                            } else if (uiState.password.length >= 8) {
                                Text(
                                    text = "✓ Longitud válida",
                                    color = TelematicsGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        },
                        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                            errorBorderColor = SeverityCrimson
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onLoginClick() }
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Responsive Row for 360dp compact screens
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onRememberMeToggle(!uiState.rememberMe) }
                            )
                        ) {
                            Checkbox(
                                checked = uiState.rememberMe,
                                onCheckedChange = onRememberMeToggle,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryBlue,
                                    uncheckedColor = Slate400,
                                    checkmarkColor = Color.White
                                )
                            )
                            Text(
                                text = "Recordar",
                                color = Slate800,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = PrimaryBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            lineHeight = 17.sp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(onClick = onForgotPasswordClick)
                        )
                    }
                }
            }

            // Error Banner
            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFEF2F2))
                        .border(BorderStroke(1.dp, Color(0xFFFCA5A5)), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = Color(0xFFB91C1C),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.horizontalGradient(listOf(PrimaryBlue, Color(0xFF1748B8))))
                    .scaleOnPress(
                        onClick = onLoginClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Iniciar Sesión",
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

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Slate200,
                    thickness = 1.dp
                )
                Text(
                    text = "O CONTINÚA CON",
                    color = Slate500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Slate200,
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Social sign-in affordances match the reference. Only the backend-supported provider is enabled.
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SocialLoginButton("Google", modifier = Modifier.weight(1f), onClick = {}) {
                    Icon(googleLogo, contentDescription = "Google", tint = Color.Unspecified, modifier = Modifier.size(22.dp))
                }
                SocialLoginButton("Apple", modifier = Modifier.weight(1f), onClick = {}) {
                    Icon(appleLogo, contentDescription = "Apple", tint = Slate900, modifier = Modifier.size(22.dp))
                }
                SocialLoginButton("Facebook", modifier = Modifier.weight(1f), onClick = {}) {
                    Box(
                        modifier = Modifier.size(22.dp).clip(CircleShape).background(Color(0xFF1877F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("f", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes una cuenta? ",
                    color = Slate600,
                    fontSize = 14.sp
                )
                Text(
                    text = "Regístrate aquí",
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onNavigateToRegister)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            }
            }
        }
    }
}

private object LoginHeroShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(
            Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.88f)
                cubicTo(size.width * 0.78f, size.height * 0.99f, size.width * 0.28f, size.height * 0.90f, 0f, size.height * 0.78f)
                close()
            }
        )
}

@Composable
private fun SocialLoginButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Slate200),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) { icon() }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    ShiftIQTheme {
        LoginScreen(
            uiState = LoginUiState(
                email = "usuario@empresa.com"
            ),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {},
            onLoginSuccess = {}
        )
    }
}
