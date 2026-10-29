plugins {
    id("pft.kotlin-core")
    // Publishes the in-memory fakes and the audit matcher once, for this module's tests and for
    // the tests of every module that depends on :core:ledger (shared, not copied; plan §3.2 #14).
    `java-test-fixtures`
}

dependencies {
    // Paise appears in this module's public API (amount validation, limits).
    api(project(":core:money"))

    // The audit matcher is a Kotest Matcher; fixture consumers need Kotest to use it.
    testFixturesApi(libs.kotest.assertions.core)
}
