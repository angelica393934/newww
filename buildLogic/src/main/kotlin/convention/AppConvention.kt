package convention

import com.android.build.api.dsl.ApplicationExtension
import config.ProjectConfig
import config.VersionHandler
import utils.applyPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import utils.implementation
import utils.implementationBundle

class AppConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugin("android-application")

            extensions.configure<ApplicationExtension> {
                compileSdk = ProjectConfig.COMPILE_SDK

                defaultConfig {
                    applicationId = ProjectConfig.APP_ID
                    versionCode = VersionHandler.generateVersionCode(target)
                    versionName = VersionHandler.generateVersionName(target)
                    minSdk = ProjectConfig.MIN_SDK
                    targetSdk = ProjectConfig.TARGET_SDK
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    manifestPlaceholders["deepLinkScheme"] = providers.gradleProperty("deepLink.scheme").get()
                    manifestPlaceholders["deepLinkHost"] = providers.gradleProperty("deepLink.host").get()
                }

                compileOptions {
                    sourceCompatibility = ProjectConfig.javaVersion
                    targetCompatibility = ProjectConfig.javaVersion
                }

                buildTypes {
                    getByName("debug") {
                        applicationIdSuffix = ".debug"
                        isDebuggable = true
                    }
                    create("staging") {
                        initWith(getByName("debug"))
                        applicationIdSuffix = ".staging"
                        matchingFallbacks.add("debug")
                    }
                    getByName("release") {
                        isMinifyEnabled = true
                        optimization {
                            enable = false
                        }
                    }
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
                implementation("tink")
                implementation("kotlinx-collections-immutable")
                implementation("kotlinx-coroutines-android")
                implementation("kotlinx-datetime")
                implementationBundle("paging")
                implementationBundle("lifecycle")
                implementationBundle("datastore")
            }
        }
    }
}
