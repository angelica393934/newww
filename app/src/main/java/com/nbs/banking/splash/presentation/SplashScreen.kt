package com.nbs.banking.splash.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.nbs.banking.R
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

@Composable
fun SplashScreen(
    alpha: Animatable<Float, AnimationVector1D>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bsb.Color.baseColor.primary1),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alpha.value)
        ) {
            // NBS Logo Badge
            Box(
                modifier = Modifier
                    .size(Dimension.SIZE_96)
                    .clip(CircleShape)
                    .background(Bsb.Color.baseColor.gray50),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.title_app_logo),
                    color = Bsb.Color.baseColor.primary1,
                    style = Bsb.Font.heading2,
                )
            }

            Spacer(modifier = Modifier.height(Dimension.SIZE_24))

            Text(
                text = stringResource(R.string.title_app_name_splash),
                color = Bsb.Color.baseColor.gray50,
                style = Bsb.Font.displayHero
            )

            Spacer(modifier = Modifier.height(Dimension.SIZE_8))

            Text(
                text = stringResource(R.string.message_app_slogan),
                color = Bsb.Color.baseColor.gray100,
                style = Bsb.Font.text1
            )
        }
    }
}
