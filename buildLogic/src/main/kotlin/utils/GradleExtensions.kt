package utils

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Internal accessor for the 'libs' version catalog.
 * * Acts as a bridge to allow binary plugins (.kt) to see the same libraries
 * as script plugins (.gradle.kts).
 */
private val Project.versionCatalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Shorthand for: plugins { id("...") }
 */
fun Project.applyPlugin(alias: String) {
    versionCatalog.findPlugin(alias).ifPresent { plugin ->
        pluginManager.apply(plugin.get().pluginId)
    }
}

/**
 * Shorthand for: dependencies { implementation(libs.xxx) }
 */
fun Project.implementation(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("implementation", it) }
}

/**
 * Shorthand for: dependencies { debugImplementation(libs.xxx) }
 */
fun Project.debugImplementation(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("debugImplementation", it) }
}

/**
 * Shorthand for: dependencies { releaseImplementation(libs.xxx) }
 */
fun Project.releaseImplementation(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("releaseImplementation", it) }
}

/**
 * Shorthand for: dependencies { ksp(libs.xxx) }
 */
fun Project.ksp(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("ksp", it) }
}

/**
 * Shorthand for: dependencies { testImplementation(libs.xxx) }
 */
fun Project.testImplementation(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("testImplementation", it) }
}

/**
 * Shorthand for: dependencies { kspTest(libs.xxx) }
 */
fun Project.kspTest(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("kspTest", it) }
}

/**
 * Shorthand for: dependencies { androidTestImplementation(libs.xxx) }
 */
fun Project.androidTestImplementation(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("androidTestImplementation", it) }
}

/**
 * Shorthand for: dependencies { kspAndroidTest(libs.xxx) }
 */
fun Project.kspAndroidTest(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("kspAndroidTest", it) }
}

/**
 * Shorthand for: dependencies { api(libs.xxx) }
 */
fun Project.api(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("api", it) }
}

/**
 * Shorthand for: dependencies { implementation(libs.bundles.xxx) }
 */
fun Project.implementationBundle(alias: String) {
    versionCatalog.findBundle(alias).ifPresent { dependencies.add("implementation", it) }
}

/**
 * Shorthand for: dependencies { api(libs.bundles.xxx) }
 */
fun Project.apiBundle(alias: String) {
    versionCatalog.findBundle(alias).ifPresent { dependencies.add("api", it) }
}

/**
 * Shorthand for: dependencies { debugImplementation(libs.bundles.xxx) }
 */
fun Project.debugImplementationBundle(alias: String) {
    versionCatalog.findBundle(alias).ifPresent { dependencies.add("debugImplementation", it) }
}

/**
 * Shorthand for: dependencies { testImplementation(libs.bundles.xxx) }
 */
fun Project.testImplementationBundle(alias: String) {
    versionCatalog.findBundle(alias).ifPresent { dependencies.add("testImplementation", it) }
}

/**
 * Shorthand for: dependencies { androidTestImplementation(libs.bundles.xxx) }
 */
fun Project.androidTestImplementationBundle(alias: String) {
    versionCatalog.findBundle(alias).ifPresent { dependencies.add("androidTestImplementation", it) }
}

/**
 * Shorthand for: dependencies { implementation(platform(libs.xxx)) }
 */
fun Project.implementationPlatform(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("implementation", dependencies.platform(it)) }
}

/**
 * Shorthand for: dependencies { androidTestImplementation(platform(libs.xxx)) }
 */
fun Project.androidTestImplementationPlatform(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("androidTestImplementation", dependencies.platform(it)) }
}

/**
 * Shorthand for: dependencies { api(platform(libs.xxx)) }
 */
fun Project.apiPlatform(alias: String) {
    versionCatalog.findLibrary(alias).ifPresent { dependencies.add("api", dependencies.platform(it)) }
}
