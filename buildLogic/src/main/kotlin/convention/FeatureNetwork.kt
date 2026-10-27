package convention

import utils.debugImplementation
import utils.implementationBundle
import utils.implementationPlatform
import utils.releaseImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project

class FeatureNetwork : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            implementationPlatform("okhttp3-bom")
            implementationBundle("okhttp")
            implementationBundle("retrofit")
            debugImplementation("chucker")
            releaseImplementation("chucker-noop")
        }
    }
}