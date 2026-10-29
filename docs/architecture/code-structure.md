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
  proguard-rules.pro                     # R8 keep rules for release (none yet; each rule must say why)
  src/main/kotlin/dev/imagh/pft/app/     # production code
    ui/theme/                            # PftTheme, ThemePrefs, BrandColors, MoneyColors, PftTypography, PftShapes,
                                         # PftSpacing (design-system tokens); TokenSheet* (token sheet: preview, catalogue, goldens)
  src/main/res/values{,-night}/          # window theme + colours (day/night; no light flash on a dark launch)
  src/main/res/font/                     # bundled Newsreader variable font (OFL-1.1, unmodified upstream)
  src/main/assets/licenses/              # licence texts that ship with bundled assets (Newsreader OFL)
  src/debug/                             # debug-only: DevContent -> DesignCatalog (token sheet + toggles), manifest removing ui-tooling's PreviewActivity
  src/release/kotlin/...                 # release twin of DevContent (empty surface)
  src/test/kotlin/dev/imagh/pft/app/     # Robolectric JVM tests (ui/theme/ for the design tokens)
  src/test/screenshots/                  # committed Roborazzi goldens (fake data only), verified by `check`
  src/testDebug/kotlin/...               # tests of debug-only code (DesignCatalogTest)
core/
  <module>/             # pure Kotlin/JVM, no Android imports
    README.md           # module doc (Purpose · Boundaries · Interface · Data · Config · Deps · Run/test)
    src/main/kotlin/dev/imagh/pft/<module>/
    src/test/kotlin/dev/imagh/pft/<module>/
    src/testFixtures/kotlin/...        # ledger only so far: fakes and matchers other modules reuse
core/shared/            # none yet (promoted on the 2nd consumer outside the ledger chain)
tools/redact/           # Python redaction tool (not part of the Gradle build)
```

Package root `dev.imagh.pft` is permanent (plan §12 Q4).

## Modules

| Module | Owns | Responsibility |
|--------|------|----------------|
| `:app` | `MainActivity`, manifest, data-extraction rules, `ui/theme` (design tokens, `PftTheme`); later: UI, Room entities/DAOs, Keystore, app lock, SafeLog | Android layer; depends on `core`, never the reverse |
| `:core:money` | `Paise`, `PaiseParser`, `PaiseFormat` (package `dev.imagh.pft.money`) | Integer-paise money: exact arithmetic, strict parsing of amount text, sign-free Indian-grouped formatting |
| `:core:ledger` | `ids/` (`Uuid7`, `NameBasedId`, typed ids), `entity/` (`EntityMeta`, `LedgerEntity`, `Auditable`, `EntityRepository`), `audit/` (`AuditEntry`, `AuditDiff`, `AuditLog`, `AuditRecorder`, `AuditedWriter`), `tx/` (`TransactionRunner`), `validation/` (`LedgerError`, `LedgerLimits`, `Validation`), `identity/` (`IdentityService`, `UserProfile`, `Device`, `IdentityRepository`), `settings/` (`SettingsService`, `AppSettingsRepository`); test fixtures in `fakes/` and `matchers/` (package `dev.imagh.pft.ledger`) | Ledger foundations: ids, identity, the audit trail and its single audited write path, atomic transactions, validation, runtime settings. Later slices add accounts, categories, tags, transactions, loans, goals |
| `build-logic` (included build) | `pft.kotlin-core`, `pft.lint` | Shared build configuration and quality gates |

Planned (added by the slice that needs them): `:core:budget` (D1), `:core:recurring` (E1). See `docs/architecture/overview.md` for the dependency direction.

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
| app UI (`ui/theme`) | `PftTheme`, `ThemePrefs`/`ThemeMode`, `BrandColors`, `MoneyColors` (colour tokens, CompositionLocals) | every screen | DS1 |
| app UI (`ui/theme`) | `PftTypography` (+ M3 type scale), `PftShapes`, `PftSpacing`, `DisplayFont` | every screen | DS2 |
| core (`:core:ledger`) | `EntityMeta<Id>` + `LedgerEntity<Id, Self>` (id, timestamps, user/device attribution, tombstone and archive state), composed into every model | every entity (account, category, tag, transaction, budget, recurring rule, loan, goal) | A3 |
| core | `Auditable` (`auditFields()`) + `AuditDiff` (generic field diff, no reflection) + `AuditEntry`/`AuditLog` | about 10 models, identity, settings | A3 |
| core | `AuditedWriter` (create, update, delete, restore, archive; one transaction, attribution, diff, audit append; a no-op writes nothing) + `AuditRecorder` | every service | A3 |
| core | `EntityRepository<T, Id>` (entityType, get, getLive, insert, update) | every repository | A3 |
| core | `LedgerError` + `Validation` (`requireName`, `requireAmount`, `requireLive`) + `LedgerLimits` | every service | A3 |
| core | `Uuid7` (time-sortable ids) and `NameBasedId` (deterministic UUIDv5 ids) | every entity; recurring-generated transactions | A3 |
| core test fixtures | `InMemoryLedgerStore` (fake runner that rolls back), `InMemoryEntityStore`, `InMemoryEntityRepository`, `LedgerTestKit`, `TestClock`, `AuditFieldsCheck` (every entity's `auditFields()` is complete, checked on each fake write), and the `shouldHaveAudited` matcher, published with `java-test-fixtures` | all core tests (`ledger`, `budget`, `recurring`) | A3 |
| app UI (`ui/theme`) | `TokenSheet` / `TokenSheetPages` (one sheet of every token) | Studio preview, debug `DesignCatalog`, `TokenSheetScreenshotTest` goldens | DS2 |

The app-data and remaining app-UI building blocks (`RoomEntityRepository`, `EntityListScreen`, ...)
are added to this table by A4a, A5, B2 and C5.
