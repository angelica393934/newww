package utils

import config.Modules
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.appModuleDeps(target: Project) {
    implementModule(target, Modules.CORE)
    implementModule(target, Modules.COMPONENTS)
    implementModule(target, Modules.NAVIGATION)
    implementModule(target, Modules.MEMBERSHIP)
    implementModule(target, Modules.CORE_DATABASE)
    implementModule(target, Modules.CORE_NETWORK)
}

fun DependencyHandler.componentsModuleDeps(target: Project) {
    implementModule(target, Modules.CORE)
}

fun DependencyHandler.coreNetworkModuleDeps(target: Project) {
    implementModule(target, Modules.CORE)
}

fun DependencyHandler.coreDatabaseModuleDeps(target: Project) {
    implementModule(target, Modules.CORE)
}

fun DependencyHandler.membershipModuleDeps(target: Project) {
    implementModule(target, Modules.CORE)
    implementModule(target, Modules.NAVIGATION)
    implementModule(target, Modules.COMPONENTS)
}

fun DependencyHandler.navigationModuleDeps(target: Project) {
    implementModule(target, Modules.CORE)
}

private fun DependencyHandler.implementModule(target: Project, moduleName: String) {
    val sourceProject = target.rootProject.findProject(":$moduleName")
    println("Project: ${target.name} using SOURCE project: :$moduleName")
    if (sourceProject != null) {
        implementation(sourceProject)
    } else {
        throw GradleException("Module $moduleName not found in project ${target.name}")
    }
}

private fun DependencyHandler.apiInternalModule(target: Project, moduleName: String) {
    val sourceProject = target.rootProject.findProject(":$moduleName")
    if (sourceProject != null) {
        println("Project: ${target.name} using SOURCE project: :$moduleName")
        api(sourceProject)
    } else {
        throw GradleException("Module $moduleName not found in project ${target.name}")
    }
}