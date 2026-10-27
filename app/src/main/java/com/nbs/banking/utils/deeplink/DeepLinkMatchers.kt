package com.nbs.banking.utils.deeplink

import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.withBackStack
import com.nbs.banking.home.navigation.DetailNavigationDestination
import com.nbs.banking.navigation.deeplink.DeeplinkConfig
import com.nbs.banking.navigation.destination.NavDemoADestination
import com.nbs.banking.navigation.destination.NoteListDestination
import com.nbs.banking.navigation.destination.RandomActivityIdeaDestination
import com.nbs.banking.note.navigation.AddNoteDestination
import kotlinx.serialization.serializer

val noteDeepLinkMatcher = UriDeepLinkMatcher(
    uriPattern = DeepLinkUri("${DeeplinkConfig.DEEPLINK_URI}${DeepLinkConstants.NOTE_LIST}"),
    serializer = serializer<NoteListDestination>()
)

val addNoteDeepLinkMatcher = UriDeepLinkMatcher(
    uriPattern = DeepLinkUri("${DeeplinkConfig.DEEPLINK_URI}${DeepLinkConstants.ADD_NOTE}"),
    serializer = serializer<AddNoteDestination>()
).withBackStack {
    listOf(NoteListDestination, it.key)
}

val detailDeepLinkMatcher = UriDeepLinkMatcher(
    uriPattern = DeepLinkUri("${DeeplinkConfig.DEEPLINK_URI}${DeepLinkConstants.DETAIL}/{id}"),
    serializer = serializer<DetailNavigationDestination>()
)

val activityIdeaDeepLinkMatcher = UriDeepLinkMatcher(
    uriPattern = DeepLinkUri("${DeeplinkConfig.DEEPLINK_URI}${DeepLinkConstants.ACTIVITY_IDEA}"),
    serializer = serializer<RandomActivityIdeaDestination>()
).withBackStack {
    listOf(NavDemoADestination, it.key)
}

val appDeepLinkMatchers: List<DeepLinkMatcher<*, *>> = listOf(
    noteDeepLinkMatcher,
    addNoteDeepLinkMatcher,
    activityIdeaDeepLinkMatcher,
    detailDeepLinkMatcher
)
