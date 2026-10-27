//package com.nbs.banking.components.atom.loading
//
//import androidx.compose.foundation.layout.size
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.Dp
//import com.airbnb.lottie.compose.LottieAnimation
//import com.airbnb.lottie.compose.LottieCompositionSpec
//import com.airbnb.lottie.compose.LottieConstants
//import com.airbnb.lottie.compose.animateLottieCompositionAsState
//import com.airbnb.lottie.compose.rememberLottieComposition
//
//@Composable
//fun BsbLottieLoader(
//    rawFiles: Int = R.raw.app_loading,
//    size: Dp = Dimension.SIZE_80
//) {
//    val composition by rememberLottieComposition(
//        LottieCompositionSpec.RawRes(rawFiles)
//    )
//    val progress by animateLottieCompositionAsState(
//        composition = composition,
//        iterations = LottieConstants.IterateForever
//    )
//    LottieAnimation(
//        composition = composition,
//        progress = { progress },
//        modifier = Modifier.size(size)
//    )
//}