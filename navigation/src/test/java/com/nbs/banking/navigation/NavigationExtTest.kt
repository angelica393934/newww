package com.nbs.banking.navigation

import com.nbs.banking.navigation.destination.AppNavKey
import com.nbs.banking.navigation.destination.HomeDestination
import com.nbs.banking.navigation.destination.HomeMviDestination
import com.nbs.banking.navigation.destination.NoteListDestination
import com.nbs.banking.navigation.destination.RandomActivityIdeaDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationExtTest {

    @Test
    fun `goTo adds key to backstack`() {
        val stack: MutableList<AppNavKey> = mutableListOf(HomeDestination)
        stack.goTo(HomeMviDestination)
        assertEquals(listOf(HomeDestination, HomeMviDestination), stack)
    }

    @Test
    fun `back removes last element when size is greater than 1`() {
        val stack: MutableList<AppNavKey> = mutableListOf(HomeDestination, HomeMviDestination)
        var exitCalled = false
        stack.back { exitCalled = true }

        assertEquals(listOf(HomeDestination), stack)
        assertEquals(false, exitCalled)
    }

    @Test
    fun `back invokes onExit callback when size is 1`() {
        val stack: MutableList<AppNavKey> = mutableListOf(HomeDestination)
        var exitCalled = false
        stack.back { exitCalled = true }

        assertEquals(listOf(HomeDestination), stack)
        assertTrue(exitCalled)
    }

    @Test
    fun `clearAndGoTo replaces backstack with target key`() {
        val stack: MutableList<AppNavKey> = mutableListOf(HomeDestination, HomeMviDestination, NoteListDestination)
        stack.clearAndGoTo(RandomActivityIdeaDestination)
        assertEquals(listOf(RandomActivityIdeaDestination), stack)
    }

    @Test
    fun `backToRoot reduces backstack to root element`() {
        val stack: MutableList<AppNavKey> = mutableListOf(HomeDestination, HomeMviDestination, NoteListDestination)
        stack.backToRoot()
        assertEquals(listOf(HomeDestination), stack)

        val singleStack: MutableList<AppNavKey> = mutableListOf(HomeDestination)
        singleStack.backToRoot()
        assertEquals(listOf(HomeDestination), singleStack)
    }

    @Test
    fun `backTo removes elements after last occurrence of target destination`() {
        val stack: MutableList<AppNavKey> = mutableListOf(HomeDestination, NoteListDestination, HomeDestination, HomeMviDestination)
        stack.backTo(HomeDestination)
        assertEquals(listOf(HomeDestination, NoteListDestination, HomeDestination), stack)
    }

    @Test
    fun `backUntil pops backstack until type T is top destination`() {
        val stack: MutableList<AppNavKey> = mutableListOf(HomeDestination, HomeMviDestination, NoteListDestination)
        stack.backUntil<HomeDestination>()
        assertEquals(listOf(HomeDestination), stack)

        val stack2: MutableList<AppNavKey> = mutableListOf(HomeDestination, HomeMviDestination, NoteListDestination)
        stack2.backUntil<RandomActivityIdeaDestination>()
        assertEquals(listOf(HomeDestination, HomeMviDestination, NoteListDestination), stack2)
    }
}
