import utils.coreDatabaseModuleDeps

plugins {
    alias(libs.plugins.nbs.library)
    alias(libs.plugins.nbs.feature.hilt)
    alias(libs.plugins.nbs.feature.database)
    alias(libs.plugins.nbs.feature.serialization)
    alias(libs.plugins.nbs.feature.unit.test)
    alias(libs.plugins.nbs.feature.android.test)
}

android {
    namespace = "com.nbs.banking.coredatabase"
}

dependencies {
    coreDatabaseModuleDeps(project)
}