# R8 rules for the release build (app/build.gradle.kts, buildTypes.release).
#
# Deliberately empty of -keep rules. AGP already keeps what the manifest and resources reference
# (MainActivity), and the libraries on the release runtime classpath (Compose, activity-compose)
# ship their own consumer rules. Add a rule only for something R8 reports as missing or the
# release smoke test shows broken, and comment it with the reason.
#
# Not here yet, on purpose: the SQLCipher/Room rules arrive with those dependencies (slice A4a;
# plan 2026-10-28-m1a-core-ledger.md, H3).
