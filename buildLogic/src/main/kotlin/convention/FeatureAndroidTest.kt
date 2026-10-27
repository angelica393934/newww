package convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.implementation
import utils.testImplementationBundle

class FeatureAndroidTest : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            testImplementationBundle("android-testing")
            implementation("androidx-junit-ktx")
        }
    }
}