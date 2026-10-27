package convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.applyPlugin
import utils.implementation

class FeatureSerialization : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugin("kotlin-serialization")
            implementation("kotlin-serialization")
        }
    }
}