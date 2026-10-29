# Personal Finance Tracker — Code structure

The module/package map — kept current as modules are added or responsibilities move
(CLAUDE.md Rule B). For each entry: what it owns and its one-line responsibility.

## Layout

```
settings.gradle.kts, build.gradle.kts, gradle.properties
gradle/                 # libs.versions.toml (all pins), wrapper, daemon JVM, verification-metadata.xml
*.lockfile              # dependency locks: root, app/, core/*/, build-logic/, buildscript-gradle.lockfile
config/detekt/          # detekt rule overrides (ktlint rules live in the root .editorconfig)
build-logic/            # included build: pft.kotlin-core + pft.lint convention plugins
app/                    # thin Android layer (Compose, Room/SQLCipher impls, Keystore, ...)
  src/main/kotlin/dev/imagh/pft/app/     # production code
  src/test/kotlin/dev/imagh/pft/app/     # Robolectric JVM tests
core/
  <module>/             # pure Kotlin/JVM, no Android imports
    README.md           # module doc (Purpose · Boundaries · Interface · Data · Config · Deps · Run/test)
    src/main/kotlin/dev/imagh/pft/<module>/
    src/test/kotlin/dev/imagh/pft/<module>/
core/shared/            # none yet (promoted on the 2nd consumer outside the ledger chain)
tools/redact/           # Python redaction tool (not part of the Gradle build)
```

Package root `dev.imagh.pft` is permanent (plan §12 Q4).

## Modules

| Module | Owns | Responsibility |
|--------|------|----------------|
| `:app` | `MainActivity`, manifest, data-extraction rules; later: UI, Room entities/DAOs, Keystore, app lock, SafeLog | Android layer; depends on `core`, never the reverse |
| `:core:money` | (empty until A2) `Paise`, parsing, formatting | Integer-paise money; planned in A2 |
| `build-logic` (included build) | `pft.kotlin-core`, `pft.lint` | Shared build configuration and quality gates |

Planned (added by the slice that needs them): `:core:ledger` (A3), `:core:budget` (D1),
`:core:recurring` (E1). See `docs/architecture/overview.md` for the dependency direction.

## Shared packages

| Package | Provides | Consumers |
|---------|----------|-----------|
| (none yet) | | |

## Shared building blocks (reuse by layer, plan §3.4)

One shared piece per layer, extracted only when the plan names at least two consumers.

| Layer | Building block | Consumers | Built in |
|-------|----------------|-----------|----------|
| build | `pft.kotlin-core` convention plugin | every `core/*` module | A1 |
| build | `pft.lint` convention plugin (ktlint + detekt) | `pft.kotlin-core`, `:app`, root project (lints `build-logic` and build scripts) | A1 |

The core, app-data and app-UI building blocks (`EntityMeta`, `AuditedWriter`,
`RoomEntityRepository`, `EntityListScreen`, ...) are added to this table by A3, A4a, A5, B2 and C5.
