package com.tuxlogic.shiftiq.mobile.feature.core.presentation.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@Composable
fun CustomerAccountRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CustomerAccountViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CustomerAccountScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onCustomerTypeChange = viewModel::onCustomerTypeChange,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onBusinessNameChange = viewModel::onBusinessNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onDocumentNumberChange = viewModel::onDocumentNumberChange,
        onSaveProfile = viewModel::saveProfile,
        onNewEmailChange = viewModel::onNewEmailChange,
        onRequestEmailCode = viewModel::requestEmailCode,
        onEmailOtpChange = viewModel::onEmailOtpChange,
        onVerifyAndUpdateEmail = viewModel::verifyAndUpdateEmail,
        onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onChangePassword = viewModel::changePassword,
        modifier = modifier
    )
}

@Composable
fun CustomerAccountScreen(
    state: CustomerAccountUiState,
    onNavigateBack: () -> Unit,
    onCustomerTypeChange: (Boolean) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onBusinessNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onDocumentNumberChange: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onNewEmailChange: (String) -> Unit,
    onRequestEmailCode: () -> Unit,
    onEmailOtpChange: (String) -> Unit,
    onVerifyAndUpdateEmail: () -> Unit,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onChangePassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val primaryButtonShape = RoundedCornerShape(14.dp)
    val primaryButtonColors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.White)
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ShiftIQPageHeader(
            title = "Mi Perfil",
            subtitle = "Datos de cuenta y seguridad",
            showBackButton = true,
            onBackPressed = onNavigateBack
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                SectionCard(title = "Datos del cliente", icon = { Icon(Icons.Default.Person, null, tint = PrimaryBlue) }) {
                    Text("Correo actual: ${state.email.ifBlank { "No disponible" }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                        OutlinedButton(onClick = { onCustomerTypeChange(false) }, modifier = Modifier.weight(1f), enabled = !state.isSavingProfile) {
                            Text(if (state.isCorporate) "Persona" else "✓ Persona")
                        }
                        OutlinedButton(onClick = { onCustomerTypeChange(true) }, modifier = Modifier.weight(1f), enabled = !state.isSavingProfile) {
                            Text(if (state.isCorporate) "✓ Empresa" else "Empresa")
                        }
                    }
                    if (state.isCorporate) {
                        AccountInput("Razón social", state.businessName, onBusinessNameChange)
                    } else {
                        AccountInput("Nombres", state.firstName, onFirstNameChange)
                        AccountInput("Apellidos", state.lastName, onLastNameChange)
                    }
                    AccountInput("${state.documentType.ifBlank { "DNI" }}", state.documentNumber, onDocumentNumberChange, keyboardType = KeyboardType.Number)
                    AccountInput("Teléfono", state.phone, onPhoneChange, keyboardType = KeyboardType.Phone)
                    Button(
                        onClick = onSaveProfile,
                        enabled = !state.isSavingProfile,
                        shape = primaryButtonShape,
                        colors = primaryButtonColors
                    ) {
                        Text(if (state.isSavingProfile) "Guardando…" else "Guardar datos del perfil")
                    }
                }

                SectionCard(title = "Correo electrónico", icon = { Icon(Icons.Default.Mail, null, tint = PrimaryBlue) }) {
                    AccountInput("Nuevo correo", state.newEmail, onNewEmailChange, keyboardType = KeyboardType.Email)
                    Button(
                        onClick = onRequestEmailCode,
                        enabled = !state.isSendingOtp && state.newEmail.isNotBlank(),
                        shape = primaryButtonShape,
                        colors = primaryButtonColors
                    ) {
                        Text(if (state.isSendingOtp) "Enviando código…" else "Enviar código de verificación")
                    }
                    if (state.emailOtpSent) {
                        AccountInput("Código de seis dígitos", state.emailOtp, onEmailOtpChange, keyboardType = KeyboardType.Number)
                        Button(
                            onClick = onVerifyAndUpdateEmail,
                            enabled = !state.isUpdatingEmail && state.emailOtp.length == 6,
                            shape = primaryButtonShape,
                            colors = primaryButtonColors
                        ) {
                            Text(if (state.isUpdatingEmail) "Verificando…" else "Verificar y actualizar correo")
                        }
                    }
                }

                SectionCard(title = "Cambiar contraseña", icon = { Icon(Icons.Default.Lock, null, tint = PrimaryBlue) }) {
                    AccountInput("Contraseña actual", state.currentPassword, onCurrentPasswordChange, keyboardType = KeyboardType.Password)
                    AccountInput("Nueva contraseña", state.newPassword, onNewPasswordChange, keyboardType = KeyboardType.Password)
                    AccountInput("Confirmar nueva contraseña", state.confirmPassword, onConfirmPasswordChange, keyboardType = KeyboardType.Password)
                    Button(
                        onClick = onChangePassword,
                        enabled = !state.isUpdatingPassword,
                        shape = primaryButtonShape,
                        colors = primaryButtonColors
                    ) {
                        Text(if (state.isUpdatingPassword) "Actualizando…" else "Actualizar contraseña")
                    }
                }

                state.errorMessage?.let { FeedbackMessage(it, isError = true) }
                state.successMessage?.let { FeedbackMessage(it, isError = false) }
                Spacer(Modifier.height(spacing.large))
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, icon: @Composable () -> Unit, content: @Composable () -> Unit) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(spacing.strokeThin, ShiftIQBorderSlate),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(spacing.medium), verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small), verticalAlignment = Alignment.CenterVertically) {
                icon()
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider()
            content()
        }
    }
}

@Composable
private fun AccountInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (keyboardType == KeyboardType.Password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None
    )
}

@Composable
private fun FeedbackMessage(message: String, isError: Boolean) {
    val color = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
    val contentColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
    Surface(modifier = Modifier.fillMaxWidth(), color = color, shape = RoundedCornerShape(MaterialTheme.spacing.radiusSmall)) {
        Text(message, modifier = Modifier.padding(MaterialTheme.spacing.smallMedium), color = contentColor, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomerAccountScreenPreview() {
    ShiftIQTheme {
        CustomerAccountScreen(
            state = CustomerAccountUiState(
                isLoading = false,
                email = "cliente@ejemplo.com",
                firstName = "María",
                lastName = "Pérez",
                documentType = "DNI",
                documentNumber = "12345678",
                phone = "999888777"
            ),
            onNavigateBack = {},
            onCustomerTypeChange = {},
            onFirstNameChange = {},
            onLastNameChange = {},
            onBusinessNameChange = {},
            onPhoneChange = {},
            onDocumentNumberChange = {},
            onSaveProfile = {},
            onNewEmailChange = {},
            onRequestEmailCode = {},
            onEmailOtpChange = {},
            onVerifyAndUpdateEmail = {},
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onChangePassword = {}
        )
    }
}
