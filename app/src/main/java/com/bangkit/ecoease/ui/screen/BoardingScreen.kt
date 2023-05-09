package com.bangkit.ecoease.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.bangkit.ecoease.R
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
){

    val listPagerItem = listOf(
        PagerItem(R.raw.lottie_wave, "Welcome", Color(0xFFB9EDDD)),
        PagerItem(R.raw.lottie_becket_trash_can, "b", Color(0xFF02AE9A)),
        PagerItem(R.raw.lottie_becket_earth_day_animation, "Welcome", Color(0xFF1DD297)),
    )

    val pagerState = rememberPagerState()
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        HorizontalPager(
            pageCount = listPagerItem.size,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            state = pagerState,
        ){page ->
            LottieContent(
                lottieResId = listPagerItem[page].lottieResId,
                text = listPagerItem[page].text,
                backgroundColor = listPagerItem[page].color,
                isPlaying = pagerState.currentPage == page
            )
        }
        BoardingNavigation(
            modifier = modifier.align(Alignment.BottomCenter),
            pagerState = pagerState,
            lengthPager = listPagerItem.size,
            navController = navController
        )
    }
}

data class PagerItem(
    val lottieResId: Int,
    val text: String,
    val color: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoardingNavigation(
    modifier: Modifier = Modifier,
    lengthPager: Int,
    pagerState: PagerState,
    navController: NavHostController
){
    val scope = rememberCoroutineScope()
    Row(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
            .padding(horizontal = 32.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until lengthPager){
                val animateDotWidth: Dp by animateDpAsState(
                    targetValue = if (pagerState.currentPage == i) 32.dp else 16.dp,
                    animationSpec = getAnimationSpec()
                )
                val animateColorDot by animateColorAsState(
                    targetValue = if (pagerState.currentPage == i) Color.White else Color.LightGray,
                    animationSpec = getAnimationSpec()
                )

                Box(modifier = modifier
                    .height(16.dp)
                    .width(animateDotWidth)
                    .clip(CircleShape)
                    .background(animateColorDot)
                    .clickable {
                        scope.launch {
                            pagerState.scrollToPage(page = i, pageOffsetFraction = pagerState.currentPageOffsetFraction)
                        }
                    }
                )
            }
        }
        if(pagerState.currentPage == lengthPager - 1){
            Button(
                onClick = { navController.navigate(Screen.Temp.route) },
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Ayo mulai")
            }
        }
    }
}

fun <T>getAnimationSpec(): TweenSpec<T>{
    return tween(
        durationMillis = 300,
        delayMillis = 0,
        easing = LinearEasing
    )
}

@Composable
fun LottieContent(
    modifier: Modifier = Modifier,
    lottieResId: Int,
    text: String,
    backgroundColor: Color,
    isPlaying: Boolean
){
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(lottieResId)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever
    )
    Column(modifier = modifier
        .fillMaxSize()
        .background(backgroundColor)
    ) {
        LottieAnimation(
            modifier = Modifier
                .size(400.dp)
                .clip(RectangleShape),
            composition = composition,
            progress = { progress }
        )
        Text(
            text = text,
            modifier = modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h4.copy(
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewLottieScreen(){
    EcoEaseTheme {
        OnBoardingScreen(
            navController = rememberNavController()
        )
    }
}