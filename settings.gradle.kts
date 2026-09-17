pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        // google() // opcjonalnie
    }
}
rootProject.name = "quiz"
include("backend", "frontend")