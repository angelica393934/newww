package convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.implementationBundle
import utils.implementationPlatform

class FeatureFirebase : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            implementationPlatform("firebase-bom")
            implementationBundle("firebase")
            implementationBundle("firebase-performance")
        }
    }
}