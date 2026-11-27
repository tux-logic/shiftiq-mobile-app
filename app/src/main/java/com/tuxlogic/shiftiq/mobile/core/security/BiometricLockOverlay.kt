package com.tuxlogic.shiftiq.mobile.core.security

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.tuxlogic.shiftiq.mobile.core.localization.AppLanguageStore
import com.tuxlogic.shiftiq.mobile.core.localization.AppTranslations
import kotlinx.coroutines.launch

@Composable
fun BiometricLockOverlay(
    activity: FragmentActivity,
    username: String,
    onUnlocked: () -> Unit,
    onSwitchAccount: () -> Unit
) {
    var isAuthenticating by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun authenticate() {
        if (isAuthenticating) return
        isAuthenticating = true
        statusMessage = null
        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    isAuthenticating = false
                    onUnlocked()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    isAuthenticating = false
                    statusMessage = errString.toString()
                }
            }
        )
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(translated("Desbloquea ShiftIQ"))
            .setSubtitle(translated("Confirma tu identidad para continuar"))
            .setNegativeButtonText(translated("Cancelar"))
            .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()
        prompt.authenticate(promptInfo)
    }

    LaunchedEffect(Unit) { authenticate() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF2563EB))))
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(84.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.14f),
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.padding(18.dp))
        }
        Spacer(Modifier.height(22.dp))
        Text(
            translated("Desbloquea ShiftIQ"),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
        if (username.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(username, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            statusMessage ?: translated("Usa tu huella o rostro para continuar."),
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = { scope.launch { authenticate() } },
            enabled = !isAuthenticating,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0F172A)
            )
        ) {
            if (isAuthenticating) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.LockOpen, contentDescription = null)
            }
            Spacer(Modifier.size(8.dp))
            Text(
                translated(if (isAuthenticating) "Verificando…" else "Desbloquear"),
                fontWeight = FontWeight.Bold
            )
        }
        TextButton(onClick = onSwitchAccount, enabled = !isAuthenticating) {
            Text(translated("Usar otra cuenta"), color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun translated(text: String): String =
    AppTranslations.translate(text, AppLanguageStore.current)
