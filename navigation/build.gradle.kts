import utils.navigationModuleDeps

plugins {
    alias(libs.plugins.nbs.library)
    alias(libs.plugins.nbs.feature.compose)
    alias(libs.plugins.nbs.feature.serialization)
    alias(libs.plugins.nbs.feature.navigation)
    alias(libs.plugins.nbs.feature.firebase)
    alias(libs.plugins.nbs.feature.hilt)
}

android {
    namespace = "com.nbs.banking.navigation"
}

dependencies {
    navigationModuleDeps(project)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}