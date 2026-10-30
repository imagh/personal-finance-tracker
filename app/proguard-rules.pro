# R8 rules for the release build (app/build.gradle.kts, buildTypes.release).
#
# Deliberately empty of -keep rules. AGP already keeps what the manifest and resources reference
# (MainActivity), and the libraries on the release runtime classpath (Compose, activity-compose)
# ship their own consumer rules. Add a rule only for something R8 reports as missing or the
# release smoke test shows broken, and comment it with the reason.
#
# Room (A4a) needs none: room-runtime's consumer rule keeps every RoomDatabase subclass and its
# constructor (Room loads the generated PftDatabase_Impl by name), and verifyReleaseShrink checks
# that PftDatabase_Impl really survives under its own name. Room entities and DAOs are reached
# through generated code that R8 can see, so they need no rule.
#
# Not here yet, on purpose: the SQLCipher rules arrive with that dependency (A4b; narrowed in H3,
# plan 2026-10-28-m1a-core-ledger.md).
