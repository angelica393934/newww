import utils.appModuleDeps

plugins {
    alias(libs.plugins.nbs.app)
    alias(libs.plugins.nbs.feature.hilt)
    alias(libs.plugins.nbs.feature.compose)
//    alias(libs.plugins.gms.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.nbs.feature.firebase)
    alias(libs.plugins.nbs.feature.network)
    alias(libs.plugins.nbs.feature.serialization)
    alias(libs.plugins.nbs.feature.navigation)
    alias(libs.plugins.nbs.feature.unit.test)
    alias(libs.plugins.nbs.feature.android.test)
}

android {
    namespace = "com.nbs.banking"
}

dependencies {
    appModuleDeps(project)
}