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
# SQLCipher (A4b, sqlcipher-android 4.7.2). libsqlcipher.so finds its Java classes, methods and
# fields by name through JNI, where R8 cannot see the callers, so renaming or removing anything there
# fails only at runtime. The AAR ships consumer rules (keep net.zetetic.** classes with their native
# methods, constructors and the fields JNI reads), which AGP merges automatically, and the emulator
# release smoke test ran clean on those alone (opening, keying and reopening the database). The rule
# below is the conservative blanket the plan asks for on top of them; H3 narrows it once a release
# smoke test covers the whole database surface.
#
# Why each part:
#   class net.zetetic.database.** : JNI looks the classes up by their original names.
#   { *; }                        : JNI calls back and reads members by their original names too
#                                   (for example SQLiteCustomFunction.dispatchCallback, CursorWindow
#                                   and statistics fields), so none may be renamed or removed.
-keep class net.zetetic.database.** { *; }
