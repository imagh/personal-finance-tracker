package dev.imagh.pft.buildlogic

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * `pft.kotlin-core`: the convention plugin for every pure Kotlin/JVM core module
 * (`core/<module>`; CLAUDE.md §3.1: no Android imports, business logic lives here).
 *
 * Provides: JVM 17 toolchain, JUnit 5 + Kotest, ktlint + detekt (via `pft.lint`), and the
 * JaCoCo gate: `check` fails when line coverage of the module is below [MIN_LINE_COVERAGE].
 * The 90% floor is a hardcoded invariant of the constitution (CLAUDE.md Rule A), not a
 * per-module setting, so modules cannot lower it.
 */
class PftKotlinCorePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("jacoco")
            pluginManager.apply("pft.lint")

            extensions.configure<KotlinJvmProjectExtension> { jvmToolchain(JVM_VERSION) }

            dependencies {
                add("testImplementation", platform(libs.library("junit-bom").get()))
                addProvider("testImplementation", libs.library("kotest-runner-junit5"))
                addProvider("testImplementation", libs.library("kotest-assertions-core"))
                addProvider("testImplementation", libs.library("kotest-property"))
            }

            extensions.configure<JacocoPluginExtension> { toolVersion = libs.version("jacoco") }

            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
                finalizedBy(tasks.withType<JacocoReport>())
            }

            tasks.withType<JacocoReport>().configureEach {
                dependsOn(tasks.withType<Test>())
                reports.xml.required.set(true)
                reports.html.required.set(true)
            }

            val verification = tasks.withType<JacocoCoverageVerification>()
            verification.configureEach {
                dependsOn(tasks.withType<Test>())
                violationRules {
                    rule {
                        limit {
                            counter = "LINE"
                            value = "COVEREDRATIO"
                            minimum = MIN_LINE_COVERAGE.toBigDecimal()
                        }
                    }
                }
            }

            // JaCoCo SKIPS verification when there is no execution data, so a module with production
            // code and no tests at all would pass the floor. This guard closes that hole (fail closed).
            // It keys off the test SOURCES, not off build/jacoco/test.exec: a leftover exec file from an
            // earlier run (no `clean`) must not let a module whose tests were deleted pass.
            val mainSources = fileTree("src/main") { include("**/*.kt", "**/*.java") }
            val testSources = fileTree("src/test") { include("**/*.kt", "**/*.java") }
            val execData = layout.buildDirectory.file("jacoco/test.exec")
            // Captured at configuration time: `project` must not be touched in `doLast` (configuration cache).
            val modulePath = path
            val requireTests =
                tasks.register("requireTestsForProductionCode") {
                    group = "verification"
                    description =
                        "Fails when the module has production sources but no tests (or its tests ran nothing)."
                    dependsOn(tasks.withType<Test>())
                    doLast {
                        if (!mainSources.isEmpty && (testSources.isEmpty || !execData.get().asFile.exists())) {
                            throw GradleException(
                                "$modulePath has production code but no tests ran. Every core module needs " +
                                    "tests; the 90% floor cannot be skipped.",
                            )
                        }
                    }
                }

            // The coverage floor is part of `check` (and therefore of the pre-push hook).
            tasks.named("check") { dependsOn(verification, requireTests) }
        }
    }

    private companion object {
        const val JVM_VERSION = 17

        /** Minimum fraction of lines the module's own tests must cover (CLAUDE.md Rule A: 90%). */
        const val MIN_LINE_COVERAGE = "0.90"
    }
}
