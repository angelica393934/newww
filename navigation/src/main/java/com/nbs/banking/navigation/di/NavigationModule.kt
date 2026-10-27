package com.nbs.banking.navigation.di

import com.nbs.banking.navigation.utils.NavigationDataManager
import com.nbs.banking.navigation.utils.NavigationDataManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Singleton
    @Binds
    abstract fun bindNavigationDataManager(
        impl: NavigationDataManagerImpl
    ): NavigationDataManager
}