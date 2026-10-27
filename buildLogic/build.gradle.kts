plugins {
    `kotlin-dsl`
}

kotlin {
    compilerOptions {
        jvmTarget.set(
            org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        )
    }
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.gradle)
}

gradlePlugin {
    plugins {
        register("FeatureNetwork") {
            id = "libs.feature.network"
            implementationClass = "convention.FeatureNetwork"
        }
        register("FeatureHilt") {
            id = "libs.feature.hilt"
            implementationClass = "convention.FeatureHilt"
        }
        register("FeatureCompose") {
            id = "libs.feature.compose"
            implementationClass = "convention.FeatureCompose"
        }
        register("FeatureDatabase") {
            id = "libs.feature.database"
            implementationClass = "convention.FeatureDatabase"
        }
        register("FeatureFirebase") {
            id = "libs.feature.firebase"
            implementationClass = "convention.FeatureFirebase"
        }
        register("FeatureNavigation") {
            id = "libs.feature.navigation"
            implementationClass = "convention.FeatureNavigation"
        }
        register("FeatureSerialization") {
            id = "libs.feature.serialization"
            implementationClass = "convention.FeatureSerialization"
        }
        register("FeatureUnitTest") {
            id = "libs.feature.unit.test"
            implementationClass = "convention.FeatureUnitTest"
        }
        register("FeatureAndroidTest") {
            id = "libs.feature.android.test"
            implementationClass = "convention.FeatureAndroidTest"
        }
        register("LibraryConvention") {
            id = "libs.library"
            implementationClass = "convention.LibraryConvention"
        }
        register("AppConvention") {
            id = "libs.app"
            implementationClass = "convention.AppConvention"
        }
    }
}