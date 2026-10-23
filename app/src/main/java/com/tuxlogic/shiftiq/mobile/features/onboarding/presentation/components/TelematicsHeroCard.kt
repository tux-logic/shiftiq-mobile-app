package com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.R
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIqTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.OnboardingSlide
import kotlinx.coroutines.delay

/**
 * Onboarding Carousel displaying stock photography natively:
 * - Auto-scrolls periodically
 * - Supports manual swipe and tap from Top Carousel Indicator
 * - Follows Better UI concentric radius (24.dp) and clean outline
 */
@Composable
fun OnboardingImageCarousel(
    slides: List<OnboardingSlide>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    autoScrollDelayMs: Long = 4000L
) {
    // Auto-scroll loop
    LaunchedEffect(pagerState) {
        while (true) {
            delay(autoScrollDelayMs)
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(BorderStroke(1.dp, Slate200), RoundedCornerShape(24.dp))
    ) { page ->
        val slide = slides.getOrNull(page)
        if (slide != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = slide.imageRes),
                    contentDescription = slide.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

/**
 * Backward compatibility alias for TelematicsHeroCard
 */
@Composable
fun TelematicsHeroCard(
    slides: List<OnboardingSlide>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    OnboardingImageCarousel(
        slides = slides,
        pagerState = pagerState,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun OnboardingImageCarouselPreview() {
    ShiftIqTheme {
        val dummySlides = listOf(
            OnboardingSlide(R.drawable.onboarding1, "Title 1", "Subtitle 1"),
            OnboardingSlide(R.drawable.onboarding2, "Title 2", "Subtitle 2"),
            OnboardingSlide(R.drawable.onboarding3, "Title 3", "Subtitle 3")
        )
        val pagerState = rememberPagerState(pageCount = { dummySlides.size })
        Box(modifier = Modifier.padding(16.dp)) {
            OnboardingImageCarousel(
                slides = dummySlides,
                pagerState = pagerState
            )
        }
    }
}
