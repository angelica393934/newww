package com.nbs.banking.splash.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import com.nbs.banking.navigation.destination.AppNavKey
import com.nbs.banking.navigation.destination.LoginDestination
import com.nbs.banking.navigation.destination.SplashDestination
import com.nbs.banking.navigation.goTo
import com.nbs.banking.splash.presentation.SplashScreen
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

fun EntryProviderScope<AppNavKey>.splashNavigation(
    backStack: MutableList<AppNavKey>
) {
    entry<SplashDestination> {
        val alpha = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            Timber.d("Splash Screen Launched")
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
            delay(1500.milliseconds)
            backStack.clear()
            backStack.goTo(LoginDestination)

        }
        SplashScreen(alpha)
    }
}