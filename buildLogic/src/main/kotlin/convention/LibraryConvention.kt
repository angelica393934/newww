package convention

import com.android.build.api.dsl.LibraryExtension
import config.ProjectConfig
import utils.applyPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import utils.implementation

class LibraryConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugin("android-library")

            extensions.configure<LibraryExtension> {
                compileSdk = ProjectConfig.COMPILE_SDK

                buildFeatures { buildConfig = true }

                defaultConfig {
                    minSdk = ProjectConfig.MIN_SDK
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    buildConfigField("String", "DEEP_LINK_SCHEME", "\"${providers.gradleProperty("deepLink.scheme").get()}\"")
                    buildConfigField("String", "DEEP_LINK_HOST", "\"${providers.gradleProperty("deepLink.host").get()}\"")
                }

                compileOptions {
                    sourceCompatibility = ProjectConfig.javaVersion
                    targetCompatibility = ProjectConfig.javaVersion
                }

                buildTypes {
                    getByName("debug") { }
                    create("staging") {
                        initWith(getByName("debug"))
                        matchingFallbacks.add("debug")
                    }
                    getByName("release") { }
                }

                buildFeatures {
                    buildConfig = true
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                    freeCompilerArgs.add("-Xexplicit-backing-fields")
                }
            }

            with(target) {
                implementation("androidx-core-ktx")
                implementation("timber")
                implementation("paging")
                implementation("kotlinx-collections-immutable")
                implementation("kotlinx-coroutines-android")
            }
        }
    }
}
