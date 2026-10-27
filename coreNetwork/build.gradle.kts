import utils.coreNetworkModuleDeps

plugins {
    alias(libs.plugins.nbs.library)
    alias(libs.plugins.nbs.feature.hilt)
    alias(libs.plugins.nbs.feature.network)
    alias(libs.plugins.nbs.feature.serialization)
    alias(libs.plugins.nbs.feature.unit.test)
    alias(libs.plugins.nbs.feature.android.test)
}

android {
    namespace = "com.nbs.banking.corenetwork"
}

dependencies {
    coreNetworkModuleDeps(project)
}