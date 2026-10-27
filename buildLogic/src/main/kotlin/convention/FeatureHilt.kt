package convention

import utils.androidTestImplementation
import utils.applyPlugin
import utils.implementation
import utils.ksp
import utils.kspAndroidTest
import utils.kspTest
import utils.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project

class FeatureHilt : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugin("devtools-ksp")
            applyPlugin("dagger-hilt")

            implementation("hilt-android")
            implementation("androidx-hilt-lifecycle-viewmodel-compose")
            implementation("androidx-hilt-work")
            ksp("hilt-compiler")
            ksp("androidx-hilt-compiler")

            // Unit Test
            testImplementation("hilt-android-testing")
            kspTest("hilt-compiler")

            // Instrumentation Test
            androidTestImplementation("hilt-android-testing")
            kspAndroidTest("hilt-compiler")
        }
    }
}
