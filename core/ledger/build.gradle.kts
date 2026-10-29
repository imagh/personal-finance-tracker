plugins {
    id("pft.kotlin-core")
}

dependencies {
    // Paise appears in this module's public API (amount validation, limits).
    api(project(":core:money"))
}
