package config

import org.gradle.api.JavaVersion

object ProjectConfig {
    const val APP_ID = "com.nbs.banking"
    const val COMPILE_SDK = 37
    const val MIN_SDK = 26
    const val TARGET_SDK = 37
    val javaVersion = JavaVersion.VERSION_21
}
