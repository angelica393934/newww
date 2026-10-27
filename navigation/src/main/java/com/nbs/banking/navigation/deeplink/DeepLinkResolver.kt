package com.nbs.banking.navigation.deeplink

import androidx.navigation3.runtime.deeplink.BackStackMatchResult
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import com.nbs.banking.navigation.destination.AppNavKey

class DeepLinkResolver(matchers: List<DeepLinkMatcher<*, *>>) {
    private val matchers = matchers.toList()

    /**
     * Resolves an incoming deep link to the back stack it should open.
     *
     * Returns the full stack, bottom to top (e.g. [Home, Detail(42)]), or null if
     * the link is unknown or malformed. Callers treat null as "not a link we handle".
     */
    fun resolve(request: DeepLinkRequest): List<AppNavKey>?{
        // Match against every registered matcher and keep the best match.
        // Results are Comparable, so maxOrNull() picks the most specific one.
        val destinationPath = matchers.mapNotNull { it.match(request) }.maxOrNull() ?: return null

        // Normalize both result types into one list of keys:
        // - matcher built with withBackStack { } -> the synthetic stack, e.g. [Note, Detail]
        // - plain matcher                        -> a single-item stack
        val keys: List<Any> = when (destinationPath) {
            is BackStackMatchResult<*, *> -> destinationPath.backStack
            else -> listOf(destinationPath.key)
        }

        // Validate: every entry must be an AppNavKey. filterIsInstance silently drops
        // anything else, so a size mismatch means the link is malformed.
        // Fail closed: return null (link ignored) rather than opening a partial stack.
        // An empty list is not a destination either, so it is also treated as "no link".
        return keys.filterIsInstance<AppNavKey>().takeIf { it.size == keys.size && it.isNotEmpty() }
    }

}