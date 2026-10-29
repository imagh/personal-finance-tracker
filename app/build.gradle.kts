plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
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

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
}
