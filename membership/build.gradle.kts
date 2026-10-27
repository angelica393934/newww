import utils.membershipModuleDeps

plugins {
    alias(libs.plugins.nbs.library)
    alias(libs.plugins.nbs.feature.hilt)
    alias(libs.plugins.nbs.feature.compose)
    alias(libs.plugins.nbs.feature.firebase)
    alias(libs.plugins.nbs.feature.serialization)
    alias(libs.plugins.nbs.feature.navigation)
    alias(libs.plugins.nbs.feature.unit.test)
    alias(libs.plugins.nbs.feature.android.test)
}

android {
    namespace = "com.nbs.banking.membership"
}

dependencies {
    membershipModuleDeps(project)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}