package convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.implementation
import utils.ksp

class FeatureDatabase : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            implementation("room-runtime")
            ksp("room-compiler")
            implementation("androidx-sqlite")
            implementation("android-database-sqlcipher")
        }
    }
}