package dev.imagh.pft.buildlogic

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/**
 * `pft.lint`: ktlint (format + check) and detekt, configured once for every module.
 *
 * Applied by `pft.kotlin-core` (core modules), directly by `:app`, and by the root project (which
 * lints `build-logic` and the build scripts), so the style gates have a
 * single definition. The pre-commit hook runs `./gradlew ktlintFormat detekt`; `check` runs
 * `ktlintCheck` and `detekt` (both are wired into `check` by their plugins).
 */
class PftLintPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure<KtlintExtension> {
                version.set(libs.version("ktlintEngine"))
                // Rules come from the root .editorconfig. Failures fail the build (never ignored).
                ignoreFailures.set(false)
                filter { exclude { it.file.path.contains("/build/") } }
            }

            extensions.configure<DetektExtension> {
                // Plain `detekt` task: no type resolution (plan §10), so it needs no compiled classes.
                buildUponDefaultConfig = true
                config.setFrom(rootProject.file("config/detekt/detekt.yml"))
                source.setFrom(
                    "src/main/kotlin",
                    "src/test/kotlin",
                    "src/testFixtures/kotlin",
                    "src/androidTest/kotlin",
                )
            }
        }
    }
}
