plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // compileOnly: the root build loads these plugins (`apply false`), so every module sees one
    // copy. Bundling them here as well would load the Kotlin plugin twice in different classloaders.
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ktlint.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("pftLint") {
            id = "pft.lint"
            implementationClass = "dev.imagh.pft.buildlogic.PftLintPlugin"
        }
        register("pftKotlinCore") {
            id = "pft.kotlin-core"
            implementationClass = "dev.imagh.pft.buildlogic.PftKotlinCorePlugin"
        }
    }
}

// Same pinning discipline as the main build: build-logic/gradle.lockfile.
dependencyLocking {
    lockAllConfigurations()
}
