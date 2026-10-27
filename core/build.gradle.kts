plugins {
    alias(libs.plugins.nbs.library)
    alias(libs.plugins.nbs.feature.compose)
    alias(libs.plugins.nbs.feature.hilt)
    alias(libs.plugins.nbs.feature.network)
    alias(libs.plugins.nbs.feature.firebase)
    alias(libs.plugins.nbs.feature.serialization)
    alias(libs.plugins.nbs.feature.unit.test)
}

android {
    namespace = "com.nbs.banking.core"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.lifecycle)
    implementation(libs.tink)
    implementation(libs.bundles.datastore)
    implementation(libs.accompanist.permissions)
    implementation(libs.bundles.paging)
    implementation(libs.workmanager)
    implementation(libs.bundles.barcode)
    implementation(libs.androidx.biometric)
    api(libs.bundles.coil)
    implementation(libs.bundles.camera)
    implementation(libs.play.services.location)
    implementation(libs.kotlin.reflect)
    androidTestImplementation(libs.workmanager.testing)
    testImplementation(kotlin("test"))
}