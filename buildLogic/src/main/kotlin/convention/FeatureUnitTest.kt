package convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.testImplementationBundle

class FeatureUnitTest : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            testImplementationBundle("unit-testing")
        }
    }
}