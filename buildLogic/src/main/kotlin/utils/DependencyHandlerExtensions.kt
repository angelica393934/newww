package utils

import org.gradle.api.artifacts.dsl.DependencyHandler


val implementation: DependencyHandler.(Any) -> Unit =
    { dependencyNotation ->
        add("implementation", dependencyNotation)
    }

val api: DependencyHandler.(Any) -> Unit =
    { dependencyNotation ->
        add("api", dependencyNotation)
    }