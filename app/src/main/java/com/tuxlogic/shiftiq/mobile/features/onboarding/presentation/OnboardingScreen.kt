package com.tuxlogic.shiftiq.mobile.features.onboarding.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.arrowForward
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIqTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.components.GoogleAuthButton
import com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.components.OnboardingImageCarousel
import com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.components.PagerIndicator
import com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.components.PhoneAuthButton
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onDriverEmailSignInClick: () -> Unit,
    onDriverPhoneSignInClick: () -> Unit,
    onDriverAuthSuccess: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    OnboardingContent(
        state = state,
        onGoogleSignInClick = { viewModel.onGoogleSignInClicked(onDriverAuthSuccess) },
        onPhoneSignInClick = onDriverPhoneSignInClick,
        onEmailSignInClick = onDriverEmailSignInClick
    )
}

@Composable
fun OnboardingContent(
    state: OnboardingUiState,
    onGoogleSignInClick: () -> Unit,
    onPhoneSignInClick: () -> Unit,
    onEmailSignInClick: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { state.slides.size }
    )
    val coroutineScope = rememberCoroutineScope()
    val currentSlide = state.slides.getOrNull(pagerState.currentPage) ?: state.slides.first()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Carousel Pager Indicator (Interactivo y enlazado al estado del pager)
            PagerIndicator(
                pageCount = pagerState.pageCount,
                currentPage = pagerState.currentPage,
                onIndicatorClick = { targetPage ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = targetPage,
                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                        )
                    }
                },
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // 2. Carrusel de Imágenes Stock con Desfile Automático
            OnboardingImageCarousel(
                slides = state.slides,
                pagerState = pagerState
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Título y Subtítulo Reactivos al Slide Actual
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = currentSlide.title,
                    color = Slate900,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = currentSlide.subtitle,
                    color = Slate500,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 4. Botones de autenticación B2C (Google y Celular con Better-UI scaleOnPress)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GoogleAuthButton(
                    onClick = onGoogleSignInClick
                )

                PhoneAuthButton(
                    onClick = onPhoneSignInClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Enlace para iniciar sesión con correo tradicional
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onEmailSignInClick)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Iniciar sesión con correo tradicional",
                    color = PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = arrowForward,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    ShiftIqTheme {
        OnboardingContent(
            state = OnboardingUiState(),
            onGoogleSignInClick = {},
            onPhoneSignInClick = {},
            onEmailSignInClick = {}
        )
    }
}
