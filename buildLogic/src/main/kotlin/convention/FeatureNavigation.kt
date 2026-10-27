package convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.implementation

class FeatureNavigation : Plugin<Project> {
    override fun apply(target: Project) {

        with(target) {
            implementation("androidx-navigation3-runtime")
            implementation("androidx-navigation3-ui")
            implementation("androidx-lifecycle-viewmodel-navigation3")
        }
    }
}