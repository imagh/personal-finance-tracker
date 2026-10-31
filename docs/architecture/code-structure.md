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
  proguard-rules.pro                     # R8 keep rules for release (SQLCipher's JNI surface; each rule says why)
  schemas/                               # exported Room schemas, one JSON per version (committed; served to migration tests as debug assets)
  src/main/kotlin/dev/imagh/pft/app/     # production code
    ui/theme/                            # PftTheme, ThemePrefs, ThemePalette (+ colors(dark)), GraphitePalette (+ PaletteColors), MidnightPalette,
                                         # IvoryPalette, AuberginePalette (DS6b: one generated, pinned object per palette), PaletteSamples
                                         # (the four-palette swatch page), PftSurfaces (BackgroundStyle, the tonal/flat surfaces,
                                         # the glow tint and LocalPftSurfaces, DS6c, DS6d), WindowBackground (SyncWindowBackground), MoneyColors, PftTypography, PftShapes,
                                         # PftSpacing, PftMotion (design-system tokens); PftSymbol (icon registry, permanent keys),
                                         # CategoryPalette (+ CategoryColors), GlyphSize (DS3); TokenSheet* (token sheet: preview, catalogue, goldens)
    ui/common/                           # shared components: CollapsingHeaderScaffold (+ HeaderStyle, HeaderPeriod), HeaderIconButton (the only control
                                         # allowed in the header's `actions`), HeaderGlow (DS6d: the glow's pure rules and
                                         # `Modifier.headerGlow`), BottomActionBar
                                         # (+ BottomBarAction), MoneyText (+ MoneySize, MoneySemantic/MoneyTone), StatusChip (+ ChipColors),
                                         # GroupedSection, EmptyState, CategoryGlyph + GlyphSource (the badge and the icon_key parser);
                                         # *Samples.kt (HeaderSamples, MoneyTextSamples, StatusChipSamples, GroupedSectionSamples,
                                         # EmptyStateSamples, CategoryGlyphSamples: previews, catalogue and goldens share them, R8 drops them)
    data/                                # Room persistence (package dev.imagh.pft.app.data)
      PftDatabase, Migrations, AuditTriggers   # the database (v1), its migration list, the audit_entry immutability triggers
      RoomTransactionRunner                    # core TransactionRunner over withTransaction
      common/                                  # MetaColumns, MetaMapper, BaseDao, EntityMapper, RoomEntityRepository (shared by every entity)
      identity/ settings/ audit/               # entities, DAOs, Room repositories: users+devices, app settings, the audit trail (RoomAuditLog, AuditEntryMapper)
      DatabaseFactory, PreMigrationBackup      # A4b: opens pft.db through SQLCipher (key first, backup, eager open); the pre-migration copy
      DatabaseStartup                          # how opening ended: Ready(db) or KeyUnavailable(reason)
    security/                            # A4b: DbKeyManager (wrapped SQLCipher key, fail closed), DbKeyUnavailable, KekSource +
                                         # AndroidKeystoreKek (the Keystore KEK), WrappedKeyFormat (66-byte file, format v1)
    ui/startup/                          # KeyUnavailableScreen (the fail-closed screen)
    PftApplication, MainActivity         # PftApplication.startup opens the database once; MainActivity shows the app or the fail-closed screen
  src/main/res/values{,-night}/          # window theme + colours (day/night; no light flash on a dark launch)
  src/main/res/font/                     # bundled Newsreader variable font (OFL-1.1, unmodified upstream)
  src/main/res/drawable/                 # sym_<key>.xml: 92 Material Symbols Rounded vector drawables (Apache-2.0), imported by tools/material-symbols
  src/main/assets/licenses/              # licence texts that ship with bundled assets (Newsreader OFL, Material Symbols Apache-2.0)
  src/debug/                             # debug-only: DevContent -> DesignCatalog (token sheet, motion page, header, bottom-bar, money, chip, section, empty-state, glyph, accent-check and palette pages, toggles), manifest removing ui-tooling's PreviewActivity
  src/release/kotlin/...                 # release twin of DevContent (empty surface)
  src/androidTest/kotlin/dev/imagh/pft/app/  # instrumented tests on the pft35 emulator (not in `check`): DbKeyManagerTest (Keystore),
                                         # data/EncryptedDatabaseTest (SQLCipher, Room pairing, triggers, backup on the encrypted file)
  src/test/kotlin/dev/imagh/pft/app/     # Robolectric JVM tests (ui/theme/ for the design tokens, ui/common/ for the shared components,
                                         # data/ for Room: triggers, repositories, runner, audit, integration with the core services)
    testing/                             # test support: TestDatabase, RoomTestKit, MigrationTestSupport, AuditColumnsCheck + AuditedTables,
                                         # RunSuspending, SampleThing (throwaway entity for the shared-block tests)
  src/test/resources/pft-symbol-keys.txt # the pinned, permanent PftSymbol keys (removal or rename fails PftSymbolTest)
  src/test/screenshots/                  # committed Roborazzi goldens (fake data only), verified by `check`
  src/testDebug/kotlin/...               # tests of debug-only code (DesignCatalogTest) and MigrationTest (needs the debug-only schema assets)
core/
  <module>/             # pure Kotlin/JVM, no Android imports
    README.md           # module doc (Purpose · Boundaries · Interface · Data · Config · Deps · Run/test)
    src/main/kotlin/dev/imagh/pft/<module>/
    src/test/kotlin/dev/imagh/pft/<module>/
    src/testFixtures/kotlin/...        # ledger only so far: fakes and matchers other modules reuse
core/shared/            # none yet (promoted on the 2nd consumer outside the ledger chain)
tools/redact/           # Python redaction tool (not part of the Gradle build)
tools/material-symbols/ # Python importer for the Material Symbols drawables: symbols.txt (curated list), svg-sha256.txt (sources), unit tests (not part of the Gradle build)
```

Package root `dev.imagh.pft` is permanent (plan §12 Q4).

## Modules

| Module | Owns | Responsibility |
|--------|------|----------------|
| `:app` | `PftApplication`, `MainActivity`, manifest, data-extraction rules, `ui/theme` (design tokens, `PftTheme`); `data/` (Room database v1 over SQLCipher, `DatabaseFactory`, `PreMigrationBackup`, DAOs, the Room implementations of the core repositories, runner and audit log); `security/` (Keystore-wrapped database key); `ui/startup/` (fail-closed screen); later: UI, app lock, SafeLog | Android layer; depends on `core`, never the reverse |
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
| app UI (`ui/theme`) | `PftTheme`, `ThemePrefs`/`ThemeMode`, `MoneyColors` (colour tokens, CompositionLocals) | every screen | DS1 |
| app UI (`ui/theme`) | `GraphitePalette` / `PaletteColors` (the default scheme; replaces the deleted `BrandColors`) | `resolveColorScheme` | DS6a |
| app UI (`ui/theme`) | `ThemePalette` + `colors(dark)`, `MidnightPalette`, `IvoryPalette`, `AuberginePalette` (the other three curated palettes; the names are persisted by AP1) | `resolveColorScheme`, `ThemePrefs.lightPalette` / `darkPalette`, the debug catalogue toggles | DS6b |
| app UI (`ui/theme`) | `PftSurfaces` / `BackgroundStyle` / `pftSurfaces` / `LocalPftSurfaces` (tonal or flat canvas, island, edge, highlight; `PaletteColors.canvas` and `island`; `dynamicPaletteColors`) | `CollapsingHeaderScaffold`, `BottomActionBar`, `GroupedSection`, `MainActivity`, the debug catalogue toggle | DS6c |
| app UI (`ui/theme`) | `WindowBackground` (`SyncWindowBackground(window)`, with `pft_window_background` in `res/values{,-night}`) | `MainActivity` | DS6c |
| app UI (`ui/theme`) | `PftTypography` (+ M3 type scale), `PftShapes`, `PftSpacing`, `DisplayFont` | every screen | DS2 |
| core (`:core:ledger`) | `EntityMeta<Id>` + `LedgerEntity<Id, Self>` (id, timestamps, user/device attribution, tombstone and archive state), composed into every model | every entity (account, category, tag, transaction, budget, recurring rule, loan, goal) | A3 |
| core | `Auditable` (`auditFields()`) + `AuditDiff` (generic field diff, no reflection) + `AuditEntry`/`AuditLog` | about 10 models, identity, settings | A3 |
| core | `AuditedWriter` (create, update, delete, restore, archive; one transaction, attribution, diff, audit append; a no-op writes nothing) + `AuditRecorder` | every service | A3 |
| core | `EntityRepository<T, Id>` (entityType, get, getLive, insert, update) | every repository | A3 |
| core | `LedgerError` + `Validation` (`requireName`, `requireAmount`, `requireLive`) + `LedgerLimits` | every service | A3 |
| core | `Uuid7` (time-sortable ids) and `NameBasedId` (deterministic UUIDv5 ids) | every entity; recurring-generated transactions | A3 |
| core test fixtures | `InMemoryLedgerStore` (fake runner that rolls back), `InMemoryEntityStore`, `InMemoryEntityRepository`, `LedgerTestKit`, `TestClock`, `AuditFieldsCheck` (every entity's `auditFields()` is complete, checked on each fake write), and the `shouldHaveAudited` matcher, published with `java-test-fixtures` | all core tests (`ledger`, `budget`, `recurring`) | A3 |
| app data | `MetaColumns` (`@Embedded` shared columns, explicit snake_case names) + `MetaMapper` (`EntityMeta` <-> columns, the one mapping) | every Room entity | A4a |
| app data | `EpochMillis` (`of`: the one instant -> epoch-ms conversion, refuses sub-millisecond; `clock()`: the millisecond `Clock` the app gives core) | every mapper, the settings repository, A5's container | A4a |
| app data | `BaseDao<E>` (insert, update returning the row count, `byId`) | every entity DAO | A4a |
| app data | `EntityMapper<T, E>` + `RoomEntityRepository<T, E, Id>` (get, getLive, insert, update; supplies `entityType`, refuses another kind, fails an update that matched no row) | every Room repository | A4a |
| app data | `RoomTransactionRunner`, `RoomAuditLog` (+ `AuditEntryMapper`), `PftDatabase.create` (the only database builder, `internal` to `:app` so only `DatabaseFactory` builds the production database; adds the migrations and `AuditTriggers.callback`, which re-installs the audit triggers on every open) | every service (runner, audit); every database (`DatabaseFactory` passes only its SQLCipher factory) | A4a |
| app data | `DbKeyManager.useKey` (the only way to get the database key; wipes it after the block) | `DatabaseFactory` (A4b); M1c sync and the export/import key wrapping later | A4b |
| app tests | `TestDatabase` (in-memory `PftDatabase`), `RoomTestKit` (core services over Room), `runSuspending` | all app integration tests | A4a |
| app tests | `MigrationTestSupport` (schema replay + "audit triggers survive" check after every migration) | every `migrateNToN+1` test and the chain test | A4a |
| app tests | `AuditColumnsCheck` + `AuditedTables` (every column audited or allowlisted) | every entity table; each slice registers its table | A4a |
| app UI (`ui/theme`) | `TokenSheet` / `TokenSheetPages` (one sheet of every token) | Studio preview, debug `DesignCatalog`, `TokenSheetScreenshotTest` goldens | DS2 |
| app UI (`ui/theme`) | `PftMotion` (four springs, nav transitions, `iosSpring`, `Modifier.pressScale`) | every screen; A5 nav host, C4 rows and keypad, DS5 rolling digits | DS4 |
| app UI (`ui/common`) | `CollapsingHeaderScaffold` (`HeaderStyle`, `HeaderPeriod`) | A5 setup and `EditFormScaffold`, B2 `EntityListScreen`, C4 Home, D4, AP1/H1 settings | DS4 |
| app UI (`ui/common`) | `glowAlpha` / `glowEnd` / `glowStrength` / `Modifier.headerGlow` (`HeaderGlow.kt`; `PaletteColors.wash`, `PftSurfaces.glow`, `ThemePrefs.headerGlow`) | `CollapsingHeaderScaffold` (the only caller); the debug catalogue toggle | DS6d |
| app UI (`ui/common`) | `HeaderIconButton(symbol, label, onClick)` (a `PftSymbol`, a mandatory label) | the scaffold's period stepper; every header's `actions` (Home search and filter, list and settings back, budget-detail overflow) | DS6e |
| app UI (`ui/common`) | `BottomActionBar` (`BottomBarAction`) | A5 `EditFormScaffold` (all forms) and setup, C5 detail | DS4 |
| app UI (`ui/common`) | `MoneyText` (`MoneySemantic`, `MoneyTone`, `MoneySize`) on `:core:money` `PaiseFormat` | B2, C4, C5, D4, F3, G2 | DS5 |
| app UI (`ui/common`) | `StatusChip` | C4, C5, D4, E4 | DS5 |
| app UI (`ui/common`) | `GroupedSection` | C4 accounts strip, C5 detail, D4 detail, AP1/H1 settings, H2 diagnostics | DS5 |
| app UI (`ui/common`) | `EmptyState` | B2 `EntityListScreen` (all lists), C4 empty month, C5 no results | DS5 |
| app UI (`ui/theme`) | `PftSymbol` (92 Material Symbols, permanent keys, `fromKey`) | `CategoryGlyph`; chrome icons everywhere (DS6e `HeaderIconButton`, A5 nav, B4 symbol grid) | DS3 |
| app UI (`ui/theme`) | `CategoryPalette` (`PftTheme.palette`, 12 named hues, light and dark), `GlyphSize` | `CategoryGlyph`; B4 palette swatches | DS3 |
| app UI (`ui/common`) | `CategoryGlyph` + `GlyphSource` (`fromIconKey` parses `icon_key`) | B2 account type, B4, C4 rows, `CategoryPicker`, D4, E4 | DS3 |

The remaining app-UI building blocks (`EntityListScreen`, `AmountField`, `AuditHistoryList`, ...) are
added to this table by A5, B2 and C5.
