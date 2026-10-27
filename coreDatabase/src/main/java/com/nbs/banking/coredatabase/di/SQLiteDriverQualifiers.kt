package com.nbs.banking.coredatabase.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StandardDriver

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CipherDriver