// Root build. Plugins are declared here with `apply false` so each plugin is loaded once and
// shared by the modules and by build-logic (which only references them compileOnly).
// Lock the plugin classpath too (buildscript-gradle.lockfile); a compromised plugin is as bad as
// a compromised library.
buildscript {
    dependencyLocking {
        lockAllConfigurations()
    }

    // The OWASP plugin pulls bcprov 1.78 while AGP 8.7.3 pulls bcpkix/bcutil 1.77. Mixed
    // BouncyCastle versions break class initialisation in AGP's signing step
    // (validateSigningDebug). Align the whole family on the newer, patched release.
    // Remove when OWASP dependency-check and AGP converge on one BouncyCastle version.
    configurations.classpath {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.bouncycastle") {
                useVersion("1.78")
                because("one coherent BouncyCastle version for AGP signing and the OWASP plugin")
            }
        }
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.owasp.dependencycheck)
    id("pft.lint")
}

// The pre-commit hook runs `./gradlew ktlintFormat detekt` from this root project, so the build
// scripts and the build-logic included build (which the root tasks do not reach on their own) are
// linted here: root *.kts are covered by ktlint's script tasks, build-logic is added explicitly.
extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    kotlinScriptAdditionalPaths {
        include(
            fileTree("build-logic") {
                include("**/*.kt", "**/*.kts")
                exclude("**/build/**", "**/.gradle/**")
            },
        )
    }
}
extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
    source.setFrom(
        "build-logic/src",
        "build-logic/build.gradle.kts",
        "build-logic/settings.gradle.kts",
        "build.gradle.kts",
        "settings.gradle.kts",
        "app/build.gradle.kts",
        "core/money/build.gradle.kts",
    )
}

// Dependency locking (Rule C): every resolvable configuration of every module is pinned in a
// committed gradle.lockfile. Refresh deliberately with:
//   ./gradlew dependencies --write-locks   (then review the diff)
allprojects {
    dependencyLocking {
        lockAllConfigurations()
    }

    // Resolves the dependency graph of every resolvable configuration so the lockfiles are
    // complete. Only for deliberate refreshes; the full procedure (including checksum metadata,
    // which must be appended to, never regenerated from scratch) is in build-logic/README.md,
    // "Refreshing pins".
    tasks.register("resolveAndLockAll") {
        notCompatibleWithConfigurationCache("resolves configurations at execution time")
        doLast {
            // Consumable configurations (…Elements) are outgoing variants, not dependency sets.
            configurations
                .filter { it.isCanBeResolved && !it.isCanBeConsumed }
                .forEach { it.incoming.resolutionResult.allComponents }
        }
    }
}

// Vulnerability scan (Rule C). NOT part of `check`: it needs the NVD database and is slow, so it
// runs in the PR security step (docs/TECHNICAL.md §5): `./gradlew dependencyCheckAggregate`.
// The API key comes from the environment only and is never committed.
//
// The gate covers what ships: the app's release runtime classpath and each core module's runtime
// classpath. Build and test tooling (Kotlin compiler, ktlint/detekt, AGP's device-test platform)
// never reaches the APK, so `-PscanAll` scans every configuration report-only to keep those
// findings visible in the PR security step without making the gate permanently red.
val scanAll = providers.gradleProperty("scanAll").isPresent
dependencyCheck {
    if (scanAll) {
        failBuildOnCVSS = 11.0f // CVSS tops out at 10, so this never fails: report only.
    } else {
        failBuildOnCVSS = 7.0f
        scanConfigurations = listOf("releaseRuntimeClasspath", "runtimeClasspath")
    }
    // Each suppression carries a written reason and an expiry date; see the file header.
    suppressionFile = file("config/dependency-check/suppressions.xml").path
    nvd {
        apiKey = providers.environmentVariable("NVD_API_KEY").orNull
    }
    analyzers {
        // Sonatype OSS Index no longer answers anonymous requests; without credentials every
        // artifact errors and the analysis fails.
        ossIndex {
            username = providers.environmentVariable("OSSINDEX_USERNAME").orNull
            password = providers.environmentVariable("OSSINDEX_TOKEN").orNull
        }
    }
}

// Fail closed: without an NVD key the feed is throttled to the point of timing out, and without
// OSS Index credentials that analyzer errors on every artifact. Either would otherwise surface as
// a slow or confusing failure instead of a clear configuration error.
tasks.matching { it.name.startsWith("dependencyCheck") && it.name != "dependencyCheckPurge" }.configureEach {
    doFirst {
        val missing =
            listOf("NVD_API_KEY", "OSSINDEX_USERNAME", "OSSINDEX_TOKEN")
                .filter { System.getenv(it).isNullOrBlank() }
        check(missing.isEmpty()) {
            "Not set: ${missing.joinToString()}. Export them in your shell profile " +
                "(see docs/guides/local-setup.md); never commit them."
        }
    }
}
