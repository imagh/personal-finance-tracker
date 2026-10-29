plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.roborazzi)
    id("pft.lint")
}

kotlin {
    jvmToolchain(17)
}

android {
    // Permanent: changing the applicationId (or the release signing key) installs as a new app
    // with an empty database. See plan 2026-10-28-m1a-core-ledger.md §12, Q4.
    namespace = "dev.imagh.pft"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.imagh.pft"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            // R8 shrinks, optimises and obfuscates the release build, which also drops sample and
            // preview code nothing references (owner decision 2026-10-30). No signingConfig on
            // purpose: release signing arrives with H3 and must fail closed until then.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    lint {
        abortOnError = true
        // Upgrades are deliberate (docs/TECHNICAL.md §7.1), so "newer version available" is noise.
        disable += setOf("AndroidGradlePluginVersion", "GradleDependency", "NewerVersionAvailable")
    }

    testOptions {
        unitTests {
            // Robolectric needs the merged manifest and resources.
            isIncludeAndroidResources = true
            // Jupiter for plain JVM tests; Robolectric's JUnit 4 tests run on the Vintage engine.
            all { it.useJUnitPlatform() }
        }
    }
}

// Screenshot goldens are committed and verified by `check` (roborazzi.test.verify=true in
// gradle.properties). Re-record deliberately with `./gradlew :app:recordRoborazziDebug`.
roborazzi {
    outputDir.set(layout.projectDirectory.dir("src/test/screenshots"))
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
}

// Guard for the release R8 switch (owner decision 2026-10-30, plan 2026-10-28 H3). Preview and
// sample code (TokenSheet and friends) must not ship in the release APK, and R8 must keep the
// launcher activity. It reads R8's mapping.txt, which lists every class that survived shrinking,
// so it fails fast if minification is switched off or a keep rule drops MainActivity.
// Wired into `check`, so the pre-push hook catches R8 errors long before a release is cut.
val verifyReleaseShrink by tasks.registering {
    group = "verification"
    description = "Asserts the release R8 mapping keeps MainActivity and drops preview-only code."
    dependsOn("assembleRelease")
    val mappingFile = layout.buildDirectory.file("outputs/mapping/release/mapping.txt")
    val mustBeKept = listOf("dev.imagh.pft.app.MainActivity")
    // Compiled names (look them up in build/tmp/kotlin-classes/release if a file is renamed):
    // TokenSheetKt holds the @PreviewLightDark TokenSheetPreview; the rest is the token sheet.
    val mustBeRemoved =
        listOf(
            "dev.imagh.pft.app.ui.theme.TokenSheet",
            "dev.imagh.pft.app.ui.theme.ComposableSingletons\$TokenSheet",
        )
    doLast {
        val file = mappingFile.get().asFile
        check(file.exists()) {
            "No R8 mapping at $file: release minification is off (buildTypes.release.isMinifyEnabled)."
        }
        // Class entries are the unindented lines: "<original name> -> <obfuscated name>:".
        val classes =
            file
                .readLines()
                .filter { !it.startsWith("#") && !it.startsWith(" ") && it.contains(" -> ") }
                .map { it.substringBefore(" -> ") }
        val missing = mustBeKept.filter { it !in classes }
        val leaked = classes.filter { name -> mustBeRemoved.any { name.startsWith(it) } }
        check(missing.isEmpty()) { "R8 removed classes that must be kept: $missing" }
        check(leaked.isEmpty()) { "Preview/sample code survived into the release build: $leaked" }
    }
}

tasks.named("check") { dependsOn(verifyReleaseShrink) }
