package com.tuxlogic.shiftiq.mobile.feature.core.presentation.profile

import android.graphics.Bitmap
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.AppLanguage
import com.tuxlogic.shiftiq.mobile.core.localization.AppLanguageStore
import com.tuxlogic.shiftiq.mobile.core.localization.AppTranslations
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.core.security.BiometricPreferences
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.launch

private val ProfileBackground = Color(0xFFF8FAFC)
private val ProfileNavy = Color(0xFF0F172A)
private val ProfileBorder = Color(0xFFE2E8F0)
private val ProfileMuted = Color(0xFF64748B)

@Composable
fun ProfileRoute(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToServiceCatalog: () -> Unit = {},
    onNavigateToBranches: () -> Unit = {},
    onNavigateToOperatingHours: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refresh() }
    ProfileScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onSaveProfile = { first, last, phone, onComplete -> viewModel.updateProfile(first, last, phone, onComplete) },
        onUploadProfileImage = viewModel::uploadProfileImage,
        onNavigateBack = onNavigateBack,
        onLogout = onLogout,
        onNavigateToServiceCatalog = onNavigateToServiceCatalog,
        onNavigateToBranches = onNavigateToBranches,
        onNavigateToOperatingHours = onNavigateToOperatingHours
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(
    uiState: ProfileUiState,
    onRefresh: () -> Unit,
    onSaveProfile: (firstName: String, lastName: String, phone: String, onComplete: (Boolean) -> Unit) -> Unit,
    onUploadProfileImage: (bytes: ByteArray, fileName: String, mimeType: String) -> Unit,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToServiceCatalog: () -> Unit,
    onNavigateToBranches: () -> Unit,
    onNavigateToOperatingHours: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showEditProfile by remember { mutableStateOf(false) }
    var showAvatarOptions by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    var photoError by remember { mutableStateOf<String?>(null) }
    var biometricAvailable by remember { mutableStateOf(false) }
    var biometricEnabled by remember(uiState.userId) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.userId) {
        if (uiState.userId.isBlank()) {
            biometricAvailable = false
            biometricEnabled = false
        } else {
            biometricAvailable = BiometricManager.from(context).canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            ) == BiometricManager.BIOMETRIC_SUCCESS
            biometricEnabled = BiometricPreferences.isEnabled(context, uiState.userId)
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        if (!enabled) {
            BiometricPreferences.setEnabled(context, uiState.userId, false)
            biometricEnabled = false
            Toast.makeText(
                context,
                AppTranslations.translate("Desbloqueo biométrico desactivado", AppLanguageStore.current),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val activity = context as? FragmentActivity ?: return
        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    BiometricPreferences.setEnabled(context, uiState.userId, true)
                    biometricEnabled = true
                    Toast.makeText(
                        context,
                        AppTranslations.translate("Desbloqueo biométrico activado", AppLanguageStore.current),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Toast.makeText(context, errString, Toast.LENGTH_SHORT).show()
                }
            }
        )
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(AppTranslations.translate("Desbloqueo biométrico", AppLanguageStore.current))
            .setSubtitle(AppTranslations.translate("Confirma tu identidad para activar el desbloqueo rápido", AppLanguageStore.current))
            .setNegativeButtonText(AppTranslations.translate("Cancelar", AppLanguageStore.current))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()
        prompt.authenticate(promptInfo)
    }

    fun submitImage(bytes: ByteArray, fileName: String, mimeType: String) {
        onUploadProfileImage(bytes, fileName, mimeType)
    }

    val galleryPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            runCatching {
                val bytes = requireNotNull(context.contentResolver.openInputStream(uri)?.use { it.readBytes() })
                require(bytes.isNotEmpty())
                submitImage(bytes, "profile.jpg", context.contentResolver.getType(uri) ?: "image/jpeg")
            }.onFailure { photoError = "No se pudo leer la imagen seleccionada." }
        }
    }
    val cameraPicker = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
            submitImage(output.toByteArray(), "profile.jpg", "image/jpeg")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ProfileBackground,
        topBar = {
            ShiftIQPageHeader(
                title = "Perfil",
                subtitle = uiState.role,
                showBackButton = true,
                onBackPressed = onNavigateBack
            )
        }
    ) { insets ->
        when {
            uiState.isLoading && uiState.profile == null -> Box(
                Modifier.fillMaxSize().padding(insets),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = PrimaryBlue) }
            uiState.errorMessage != null && uiState.profile == null -> PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize().padding(insets)
            ) {
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
            else -> PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize().padding(insets)
            ) {
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                ProfileIdentityCard(
                    uiState = uiState,
                    onClickAvatar = { showAvatarOptions = true }
                )

                ProfileSectionCard(
                    icon = Icons.Default.Badge,
                    title = "Información personal"
                ) {
                    ProfileInfoRow("Documento", uiState.document)
                    Spacer(Modifier.height(10.dp))
                    ProfileInfoRow("Teléfono", uiState.phone)
                    Spacer(Modifier.height(10.dp))
                    ProfileInfoRow("Correo", uiState.email)
                }

                if (uiState.isOwnerAdmin || uiState.isEmployee) {
                    ProfileSectionCard(
                        icon = if (uiState.isOwnerAdmin) Icons.Default.Business else Icons.Default.Storefront,
                        title = if (uiState.isOwnerAdmin) "Organización y sede activa" else "Sede asignada"
                    ) {
                        ProfileInfoRow("Sede activa", uiState.activeBranchName)
                        if (uiState.isEmployee) {
                            Spacer(Modifier.height(10.dp))
                            ProfileInfoRow("Especialidad", uiState.specialtyName)
                            if (uiState.salary > 0) {
                                Spacer(Modifier.height(10.dp))
                                ProfileInfoRow("Sueldo base", "S/ ${"%.2f".format(uiState.salary)}")
                            }
                        }
                    }
                }

                if (uiState.isOwnerAdmin) {
                    ProfileSectionCard(icon = Icons.Default.Tune, title = "Configuración del taller") {
                        ProfileActionTile(
                            icon = Icons.Default.DesignServices,
                            title = "Catálogo de servicios",
                            subtitle = "Gestiona los servicios y tarifas de la sede",
                            onClick = onNavigateToServiceCatalog
                        )
                        ProfileActionDivider()
                        ProfileActionTile(
                            icon = Icons.Default.Storefront,
                            title = "Sedes del taller",
                            subtitle = "Cambia o administra la sede activa",
                            onClick = onNavigateToBranches
                        )
                        ProfileActionDivider()
                        ProfileActionTile(
                            icon = Icons.Default.Schedule,
                            title = "Horarios de atención",
                            subtitle = "Configura los horarios de la sede",
                            onClick = onNavigateToOperatingHours
                        )
                    }
                }

                if (biometricAvailable) {
                    ProfileSectionCard(icon = Icons.Default.Fingerprint, title = "Seguridad") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Desbloqueo con huella o rostro", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Usa la biometría para iniciar sesión rápidamente", color = ProfileMuted, fontSize = 12.sp)
                            }
                            Switch(checked = biometricEnabled, onCheckedChange = ::toggleBiometric)
                        }
                    }
                }

                ProfileSectionCard(icon = Icons.Default.Language, title = "Idioma") {
                    val isSpanish = AppLanguageStore.current == AppLanguage.SPANISH
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = isSpanish,
                            onClick = { AppLanguageStore.set(context, AppLanguage.SPANISH) },
                            label = { Text("Español") }
                        )
                        FilterChip(
                            selected = !isSpanish,
                            onClick = { AppLanguageStore.set(context, AppLanguage.ENGLISH) },
                            label = { Text("English") }
                        )
                    }
                }

                Button(
                    onClick = { showEditProfile = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Editar perfil", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showLogoutConfirmation = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFDC2626))
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar sesión", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
            }
            }
        }
    }

    if (showAvatarOptions) {
        ModalBottomSheet(
            onDismissRequest = { showAvatarOptions = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                ProfileActionTile(
                    icon = Icons.Default.PhotoCamera,
                    title = "Tomar foto",
                    subtitle = "Usa la cámara del dispositivo",
                    onClick = {
                        showAvatarOptions = false
                        cameraPicker.launch(null)
                    }
                )
                ProfileActionTile(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Elegir de la galería",
                    subtitle = "Selecciona una foto guardada",
                    onClick = {
                        showAvatarOptions = false
                        galleryPicker.launch("image/*")
                    }
                )
            }
        }
    }

    if (showEditProfile) {
        EditProfileSheet(
            uiState = uiState,
            onDismiss = { showEditProfile = false },
            onSave = { first, last, phone ->
                onSaveProfile(first, last, phone) { success ->
                    if (success) scope.launch { showEditProfile = false }
                }
            }
        )
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Quieres cerrar la sesión actual?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirmation = false
                    onLogout()
                }) { Text("Cerrar sesión", color = Color(0xFFDC2626)) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) { Text("Cancelar") }
            }
        )
    }

    photoError?.let { message ->
        AlertDialog(
            onDismissRequest = { photoError = null },
            title = { Text("Foto de perfil") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = { photoError = null }) { Text("Aceptar") } }
        )
    }
}

@Composable
private fun ProfileIdentityCard(uiState: ProfileUiState, onClickAvatar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(ProfileNavy, Color(0xFF1E3A8A))))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size(88.dp), contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(80.dp).clip(CircleShape),
                color = Color.White.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (!uiState.profileImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = uiState.profileImageUrl,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Text(
                            uiState.name.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    if (uiState.isUploadingImage) CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                }
            }
            Box(
                modifier = Modifier.size(26.dp).clip(CircleShape)
                    .background(Color(0xFF2563EB)).border(1.5.dp, Color.White, CircleShape)
                    .clickable(onClick = onClickAvatar),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Cambiar foto", tint = Color.White, modifier = Modifier.size(14.dp))
            }
            Box(Modifier.size(88.dp).clip(CircleShape).clickable(onClick = onClickAvatar))
        }
        Spacer(Modifier.height(12.dp))
        Text(
            uiState.name.ifBlank { "Usuario ShiftIQ" },
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(4.dp))
        Text(uiState.email, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            uiState.role,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun ProfileSectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(Color.White).border(1.dp, ProfileBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(21.dp))
            Text(title, color = Color(0xFF1E293B), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE8EDF4)))
        Spacer(Modifier.height(14.dp))
        content()
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = ProfileMuted, fontSize = 13.sp, modifier = Modifier.weight(0.9f))
        Text(
            value.ifBlank { "-" },
            color = Color(0xFF1E293B),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.1f)
        )
    }
}

@Composable
private fun ProfileActionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFE9EDF8)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(23.dp)) }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, color = ProfileMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun ProfileActionDivider() {
    Spacer(Modifier.fillMaxWidth().padding(start = 60.dp).height(1.dp).background(Color(0xFFF1F5F9)))
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EditProfileSheet(
    uiState: ProfileUiState,
    onDismiss: () -> Unit,
    onSave: (firstName: String, lastName: String, phone: String) -> Unit
) {
    val employee = uiState.profile?.employee
    val owner = uiState.profile?.owner
    var firstName by remember(uiState.profile) { mutableStateOf(employee?.firstName ?: owner?.firstName.orEmpty()) }
    var lastName by remember(uiState.profile) { mutableStateOf(employee?.lastName ?: owner?.lastName.orEmpty()) }
    var phone by remember(uiState.profile) { mutableStateOf(employee?.phone ?: owner?.phone.orEmpty()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Editar perfil", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Cerrar") }
            }
            OutlinedTextField(firstName, { firstName = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(lastName, { lastName = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(phone, { phone = it }, label = { Text("Teléfono móvil") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Button(
                onClick = { if (firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()) onSave(firstName, lastName, phone) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !uiState.isSaving && firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()
            ) {
                if (uiState.isSaving) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                else Text("Guardar")
            }
        }
    }
}
