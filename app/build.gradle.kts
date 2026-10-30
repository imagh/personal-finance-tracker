plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
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
        // Instrumented tests (src/androidTest) run on the pft35 emulator, only for what the JVM
        // cannot provide: the Android Keystore and SQLCipher's native library (plan §3.2 #14).
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    sourceSets {
        // MigrationTestHelper reads the exported schemas (app/schemas/<db class>/<version>.json) as
        // assets, and Robolectric serves the tested variant's merged assets. They go into the debug
        // build only (a few KB of table definitions, no data) so the release APK does not carry
        // them; for that reason MigrationTest lives in src/testDebug.
        getByName("debug").assets.srcDir("$projectDir/schemas")
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

// Room exports one JSON schema per database version. They are committed: the migration tests
// replay them and a reviewer sees every schema change as a diff (docs/TECHNICAL.md §6).
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

// Screenshot goldens are committed and verified by `check` (roborazzi.test.verify=true in
// gradle.properties). Re-record deliberately with `./gradlew :app:recordRoborazziDebug`.
roborazzi {
    outputDir.set(layout.projectDirectory.dir("src/test/screenshots"))
}

dependencies {
    implementation(project(":core:money"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)

    // Business logic and the repository interfaces this module implements (CLAUDE.md §3.1).
    implementation(project(":core:ledger"))
    implementation(libs.room.runtime)
    // Encrypts pft.db (SEC-1); loaded by DatabaseFactory, keyed by DbKeyManager.
    implementation(libs.sqlcipher.android)
    ksp(libs.room.compiler)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.compose.ui.test.junit4)
    // The in-memory fakes, TestClock and Kotest assertions that core publishes once (plan §3.2 #14).
    testImplementation(testFixtures(project(":core:ledger")))
    testImplementation(libs.room.testing)
    // Room entities for the shared-block tests (a throwaway database, never shipped).
    kspTest(libs.room.compiler)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.junit4)
}

// Guard for the release R8 switch (owner decision 2026-10-30, plan 2026-10-28 H3). Preview and
// sample code (TokenSheet and friends) must not ship in the release APK, and R8 must keep the
// launcher activity. It reads R8's mapping.txt, so it fails fast if minification is switched off
// or a keep rule drops MainActivity. Wired into `check`, so the pre-push hook catches R8 errors
// long before a release is cut.
//
// mapping.txt also lists classes R8 merged or inlined, with the target `R8$$REMOVED$$CLASS$$N`.
// They are not in the APK, so they count as removed, never as kept.
val verifyReleaseShrink by tasks.registering {
    group = "verification"
    description = "Asserts the release R8 mapping keeps MainActivity and drops preview-only code."
    dependsOn("assembleRelease")
    val mappingFile = layout.buildDirectory.file("outputs/mapping/release/mapping.txt")
    val preShrinkClasses = layout.buildDirectory.dir("tmp/kotlin-classes/release")
    // Kept, and unobfuscated, so each must map to its own name. MainActivity: the manifest names it.
    // PftDatabase_Impl: Room's generated database is created by name through reflection
    // (Room.getGeneratedImplementation); room-runtime's consumer rule keeps it and its constructor,
    // and nothing in this module references it yet, so only that rule stops R8 removing it.
    // SQLiteConnection: libsqlcipher.so registers its natives on it by name through JNI, which R8
    // cannot see, so it survives only because of the SQLCipher keep rules (the AAR's own and
    // proguard-rules.pro).
    val mustBeKept =
        listOf(
            "dev.imagh.pft.app.MainActivity",
            "dev.imagh.pft.app.data.PftDatabase_Impl",
            "net.zetetic.database.sqlcipher.SQLiteConnection",
        )
    // Compiled names (look them up in build/tmp/kotlin-classes/release if a file is renamed):
    // TokenSheetKt holds the @PreviewLightDark TokenSheetPreview; the rest is the token sheet.
    // HeaderSamplesKt holds DS4's fake-data header samples and their @PreviewLightDark previews.
    // DS3 adds CategoryGlyphSamplesKt (the symbol grid, the source samples and the preview).
    // DS5 adds one *SamplesKt per component (GroupedSectionSamplesKt, MoneyTextSamplesKt, ...) holding
    // its fake-data samples and previews. DS6b adds PaletteSamplesKt (the four-palette swatch page).
    // Each prefix must match a class before shrinking, or the entry is stale and proves nothing.
    val mustBeRemoved =
        listOf(
            "dev.imagh.pft.app.ui.theme.TokenSheet",
            "dev.imagh.pft.app.ui.theme.ComposableSingletons\$TokenSheet",
            "dev.imagh.pft.app.ui.common.HeaderSamples",
            "dev.imagh.pft.app.ui.common.ComposableSingletons\$HeaderSamples",
            "dev.imagh.pft.app.ui.common.EmptyStateSamples",
            "dev.imagh.pft.app.ui.common.ComposableSingletons\$EmptyStateSamples",
            "dev.imagh.pft.app.ui.common.GroupedSectionSamples",
            "dev.imagh.pft.app.ui.common.ComposableSingletons\$GroupedSectionSamples",
            "dev.imagh.pft.app.ui.common.MoneyTextSamples",
            "dev.imagh.pft.app.ui.common.ComposableSingletons\$MoneyTextSamples",
            "dev.imagh.pft.app.ui.common.StatusChipSamples",
            "dev.imagh.pft.app.ui.common.ComposableSingletons\$StatusChipSamples",
            "dev.imagh.pft.app.ui.common.CategoryGlyphSamples",
            "dev.imagh.pft.app.ui.common.ComposableSingletons\$CategoryGlyphSamples",
            "dev.imagh.pft.app.ui.theme.PaletteSamples",
            "dev.imagh.pft.app.ui.theme.ComposableSingletons\$PaletteSamples",
        )
    doLast {
        val classesDir = preShrinkClasses.get().asFile
        val compiled =
            classesDir
                .walkTopDown()
                .filter { it.isFile && it.extension == "class" }
                .map {
                    it
                        .relativeTo(classesDir)
                        .path
                        .removeSuffix(".class")
                        .replace('/', '.')
                }.toList()
        mustBeRemoved.forEach { prefix ->
            check(compiled.any { it.startsWith(prefix) }) {
                "stale guard entry: $prefix matches no compiled class, update mustBeRemoved"
            }
        }

        val file = mappingFile.get().asFile
        check(file.exists()) {
            "No R8 mapping at $file: release minification is off (buildTypes.release.isMinifyEnabled)."
        }
        // Class entries are the unindented lines: "<original name> -> <target name>:".
        val targets =
            file
                .readLines()
                .filter { !it.startsWith("#") && !it.startsWith(" ") && it.contains(" -> ") }
                .associate { it.substringBefore(" -> ") to it.substringAfter(" -> ").removeSuffix(":") }
        val shipped = targets.filterValues { !it.startsWith("R8\$\$REMOVED\$\$CLASS\$\$") }
        val notKept = mustBeKept.filter { shipped[it] != it }
        val leaked = shipped.keys.filter { name -> mustBeRemoved.any { name.startsWith(it) } }
        check(notKept.isEmpty()) { "R8 removed or renamed classes that must be kept as they are: $notKept" }
        check(leaked.isEmpty()) { "Preview/sample code survived into the release build: $leaked" }
    }
}

tasks.named("check") { dependsOn(verifyReleaseShrink) }
