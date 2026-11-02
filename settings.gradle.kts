// Google's repository is restricted to the groups it actually serves (the groups resolved by this
// build are in the lockfiles), so a dependency-confusion attempt against a public group name
// cannot be satisfied from the wrong repository. The filter appears twice because settings blocks
// cannot share a value; keep both in sync.
pluginManagement {
    // Convention plugins (pft.kotlin-core, pft.lint); see build-logic/README.md.
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android(\\..*)?")
                includeGroupByRegex("com\\.google\\.android(\\..*)?")
                includeGroupByRegex("com\\.google\\.testing\\.platform(\\..*)?")
                includeGroupByRegex("androidx(\\..*)?")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android(\\..*)?")
                includeGroupByRegex("com\\.google\\.android(\\..*)?")
                includeGroupByRegex("com\\.google\\.testing\\.platform(\\..*)?")
                includeGroupByRegex("androidx(\\..*)?")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "personal-finance-tracker"

// Other core modules are added in the slice that needs them (plan §3.1).
include(":app")
include(":core:money")
include(":core:ledger")
