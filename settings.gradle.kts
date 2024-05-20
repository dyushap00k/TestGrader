@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TestGrader"

include(":app")

includeBuild("form-scanner") {
    dependencySubstitution {
        substitute(module("com.formscanner:form-scanner")).using(project(":"))
    }
}