package config

import org.gradle.api.Project

object VersionHandler {
    /*** Versioning ***/
    private const val MAJOR_VERSION = 1
    private const val MINOR_VERSION = 0
    private const val PATCH_VERSION = 0
    private const val BUMP_VERSION = 0
    private const val MAJOR_VERSION_BUILD_NUMBER = 1000000
    private const val MINOR_VERSION_BUILD_NUMBER = 10000
    private const val PATCH_VERSION_BUILD_NUMBER = 100

    fun generateVersionCode(project: Project): Int = if (!project.hasProperty("APP_BUILD_NUMBER")) {
        (MAJOR_VERSION * MAJOR_VERSION_BUILD_NUMBER) +
                (MINOR_VERSION * MINOR_VERSION_BUILD_NUMBER) +
                (PATCH_VERSION * PATCH_VERSION_BUILD_NUMBER) +
                BUMP_VERSION
    } else {
        project.property("APP_BUILD_NUMBER").toString().toIntOrNull() ?: 1
    }

    fun generateVersionName(project: Project): String {
        val preRelease = System.getenv("CI_BUILD_TYPE") ?: ""
        var versionName = "$MAJOR_VERSION.$MINOR_VERSION.$PATCH_VERSION"
        if (preRelease.isNotEmpty()) {
            versionName = "$versionName-$preRelease"
        }
        return versionName
    }
}