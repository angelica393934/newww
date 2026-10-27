package convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.applyPlugin
import org.gradle.kotlin.dsl.configure
import utils.androidTestImplementation
import utils.androidTestImplementationPlatform
import utils.debugImplementationBundle
import utils.implementation
import utils.implementationBundle
import utils.implementationPlatform

class FeatureCompose : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugin("kotlin-compose")

            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    buildFeatures {
                        compose = true
                    }
                }
            }

            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    buildFeatures {
                        compose = true
                    }
                }
            }

            implementationPlatform("androidx-compose-bom")
            androidTestImplementationPlatform("androidx-compose-bom")
            implementationBundle("compose-ui")
            implementation("androidx-activity-compose")
            implementation("androidx-window")
            debugImplementationBundle("compose-debug")
            androidTestImplementation("androidx-compose-ui-test-junit4")
        }
    }
}
