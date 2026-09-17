import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.tasks.Copy

plugins {
    id("com.github.node-gradle.node") version "5.0.0"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

node {
    download.set(true)
    version.set("18.20.0")
    npmVersion.set("9.8.0")
    nodeProjectDir.set(file("frontend"))
}

// --- Frontend tasks ---
val frontendNpmInstall by tasks.registering(NpmTask::class) {
    args.set(listOf("ci"))
    inputs.file("frontend/package.json")
    inputs.file("frontend/package-lock.json")
    outputs.dir("frontend/node_modules")
}

val frontendBuild by tasks.registering(NpmTask::class) {
    dependsOn(frontendNpmInstall)
    args.set(listOf("run", "build"))
    inputs.dir("frontend/src")
    outputs.dir("frontend/dist") // adjust to your actual output folder
}

val frontendDev by tasks.registering(NpmTask::class) {
    dependsOn(frontendNpmInstall)
    args.set(listOf("run", "dev")) // or "start", depending on your package.json
}

// --- Combined task ---
tasks.register("buildAll") {
    dependsOn(frontendBuild, ":backend:build")
}