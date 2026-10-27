package com.nbs.banking.navigation.deeplink

import com.nbs.banking.navigation.destination.AppNavKey
import kotlin.reflect.KClass

/**
 * Decides which deep links may be opened and classifies pages by access level.
 * The page sets are supplied by the app, because core cannot reference feature
 * destinations. Fails closed: anything not classified is rejected.
 *
 * @param postLoginPages pages that need a session
 * The anchor is Home. The link waits until the user is past the gate, then the target
 * is opened on top of Home, so back returns to Home.
 * withBackStack: do NOT use Home. Home is the anchor and is already in the stack by
 * then, so adding it again would duplicate it.
 *
 * @param preLoginPages gate screens a user sits on before the app proper (Splash, Login, verification...)
 * Used only to answer "is the user still at the gate?". The effect waits until none
 * of these are in the stack before opening a post-login link.
 * These are NOT deep-link targets: the ViewModel ignores links to them.
 * Any screen between login and Home (lock, terms...) must be listed here, otherwise
 * a post-login link fires too early.
 *
 * @param generalPages pages that can be accessed everywhere, logged in or not.
 * The link only waits for the splash to finish, then the target is opened on top of the
 * current screen (Login when logged out, the current screen when logged in).
 * withBackStack: do NOT include Splash, Login or Home. Splash is already gone, and
 * whichever of Login or Home applies is already in the stack. Adding Home would put a
 * post-login screen above Login when the user is logged out.
 */
class DeepLinkPolicy(
    postLoginPages: Set<KClass<out AppNavKey>>,
    preLoginPages: Set<KClass<out AppNavKey>>,
    generalPages: Set<KClass<out AppNavKey>>,
    splashPages: Set<KClass<out AppNavKey>>,
) {
    // Defensive copies, so the sets can't be changed after construction.
    private val postLoginPages = postLoginPages.toSet()
    private val preLoginPages = preLoginPages.toSet()
    private val generalPages = generalPages.toSet()

    private val splashPages = splashPages.toSet()

    fun isPostLogin(key: AppNavKey) = key::class in postLoginPages
    fun isPreLogin(key: AppNavKey) = key::class in preLoginPages
    fun isGeneral(key: AppNavKey) = key::class in generalPages
    fun isSplash(key: AppNavKey) = key::class in splashPages

    /** Access is decided by the target (the last key). Roots need no classification. */
    fun isAllowed(keys: List<AppNavKey>): Boolean {
        val target = keys.lastOrNull() ?: return false
        val accessAllowed = when {
            // Needs a session: the UI waits until the user is past the gate.
            isPostLogin(target) -> true
            // Every key must be general, or a post-login screen could end up above Login.
            isGeneral(target) -> keys.all { isGeneral(it) }
            // Unclassified targets and gate pages are not deep-linkable.
            else -> false
        }
        return accessAllowed
    }
}