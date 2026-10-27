package com.nbs.banking

import android.app.Application
import com.google.crypto.tink.aead.AeadConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BankingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        System.loadLibrary("sqlcipher")
        AeadConfig.register()
    }
}