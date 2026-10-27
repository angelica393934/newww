package com.nbs.banking.utils.deeplink

import com.nbs.banking.home.navigation.DetailNavigationDestination
import com.nbs.banking.longlist.navigation.LongListDestination
import com.nbs.banking.membership.navigation.VerificationSuccessNavigationDestination
import com.nbs.banking.navigation.deeplink.DeepLinkPolicy
import com.nbs.banking.navigation.destination.DashboardDestination
import com.nbs.banking.navigation.destination.HomeDestination
import com.nbs.banking.navigation.destination.HomeMviDestination
import com.nbs.banking.navigation.destination.LoginDestination
import com.nbs.banking.navigation.destination.NavDemoADestination
import com.nbs.banking.navigation.destination.NoteListDestination
import com.nbs.banking.navigation.destination.RandomActivityIdeaDestination
import com.nbs.banking.navigation.destination.SplashDestination
import com.nbs.banking.note.navigation.AddNoteDestination

internal val appDeepLinkPolicy = DeepLinkPolicy(
    postLoginPages = setOf(
        NoteListDestination::class,
        DetailNavigationDestination::class,
        HomeDestination::class,
        HomeMviDestination::class,
        AddNoteDestination::class,
        DashboardDestination::class,
        LongListDestination::class,
    ),
    preLoginPages = setOf(
        SplashDestination::class,
        LoginDestination::class,
        VerificationSuccessNavigationDestination::class,
    ),
    generalPages = setOf(
        NavDemoADestination::class,
        RandomActivityIdeaDestination::class,
    ),
    splashPages = setOf(
        SplashDestination::class,
    )
)