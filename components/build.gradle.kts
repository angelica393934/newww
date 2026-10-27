import utils.componentsModuleDeps
import utils.membershipModuleDeps

plugins {
    alias(libs.plugins.nbs.library)
    alias(libs.plugins.nbs.feature.compose)
    alias(libs.plugins.nbs.feature.unit.test)
}

android {
    namespace = "com.nbs.banking.components"
}

dependencies {
    componentsModuleDeps(project)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}