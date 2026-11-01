# Plan: M1a A5 — Safe logging, app container and first-run setup

| Field       | Value |
|-------------|-------|
| Date        | 2026-11-01 |
| Author      | architect |
| Status      | Approved (2026-11-01; Q1–Q4 defaults accepted, §12) |
| Related ACs | PRD L-8 (per-device user profile chosen on first run), SEC-5 (logs carry no financial content), SEC-2 (`FLAG_SECURE` on every screen, unchanged), NFR responsiveness (no database open on the main thread); M1a carried criteria: the A4a millisecond-clock criterion and the A4b off-main-thread follow-up |
| Plan owner  | akashg (repo owner) |

Foundation: this is step **A5** of the approved M1a plan, [`2026-10-28-m1a-core-ledger.md`](2026-10-28-m1a-core-ledger.md) (abbreviated **M1a** below): §6 "A5", the A3/A4a/A4b "As built" notes (`:120-127`, `:129-136`, `:228-235`) and the A5 follow-up at `:242`. It also follows the design-system amendments to A5 (M1a §3.5 table, `:147`; [`2026-10-29-m1a-design-system.md`](2026-10-29-m1a-design-system.md) §6.3) and the premium-look gates ([`2026-10-30-m1a-premium-look.md`](2026-10-30-m1a-premium-look.md) `:699-702`).

**Merged into `feat/m1a-foundation` (at `6ff2bfb`):** A1, A2, A3, A4a, A4b, DS1–DS5, DS6a–e and DS6g–i. Every hard gate before A5 has landed. A5 is the last foundation step before A-final.

---

## 1. Requirement

**Need.** A4b left the app able to open its encrypted database, but nothing uses it: `MainActivity` shows the debug design catalogue, or an empty surface in release (`app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt:37`). The database also opens on the main thread (`PftApplication.kt:18`, `DatabaseFactory.kt:34-35`), which risks an ANR from Keystore work, key derivation and the backup copy. Every later slice (B1 onwards) needs four things:
- a services container;
- a user and device to attribute writes to (L-8);
- a navigation host;
- the form building blocks that every edit screen reuses (M1a §3.4).

Logging must also be made safe by construction before any feature code has a chance to log money or names (SEC-5, CLAUDE.md §3.7).

**Done means:**
1. On a fresh install, the app opens the encrypted database **off the main thread** and shows a **Setup** screen built on the design system. The user enters their name and this phone's name and taps **Continue** in the bottom bar. The app then shows Home, and the database holds one user, one device and the `current_device_id` setting, with three audit entries.
2. Later launches go straight to Home. Back from Home leaves the app; it never returns to Setup.
3. Two startup failures **fail closed** to the "can't open your data" screen with a code, and nothing is created or overwritten:
   - the key cannot be produced (as in A4b);
   - the stored identity is inconsistent (`LedgerError.IdentityCorrupt`).
4. All logging goes through `SafeLog` with typed `LogEvent`s that cannot hold text. Direct `android.util.Log`, `println` and `printStackTrace` are rejected by detekt and by a source scan.
5. The clock that core receives yields whole milliseconds only (the A4a criterion).
6. Shared form blocks exist in `ui/common`, and Setup is their first consumer:
   - `FieldState` (with `FieldError`);
   - `NameField`;
   - `EditFormScaffold`;
   - the `LedgerError` → message mapping.
7. In debug builds, the design catalogue is a nav route; it is absent from release builds.
8. `./gradlew check` is green, and the docs below are updated.

**Out of scope:**
- Picking an existing user on Setup (see Q1). Core already supports it through `initialiseForExistingUser`, but no M1a path can produce existing users without a device. Deferred and tracked on the PRD roadmap (§6.2, R-1).
- Seeding default categories: B3 adds it to `IdentityService.initialise` and updates the A5 tests.
- The bottom `NavigationBar` (C4).
- Real Home content (C4).
- App lock and `USE_BIOMETRIC` (H1).
- The diagnostics screen (H2).
- Appearance settings (AP1).
- A screen for a failed pre-migration backup or a database that cannot be opened. These still end the start without touching data, as A4b decided (`DatabaseStartup.kt:9-12`).
- Restoring typed Setup text after process death.

## 2. Current state

**Startup path**
- `PftApplication.startup` opens the database lazily, on whichever thread first touches it (`PftApplication.kt:16-19`). Today that is the main thread, from `MainActivity.onCreate` (`MainActivity.kt:31`). M1a `:242` says A5 **retires** `PftApplication.startup` and opens the database off the main thread.
- `DatabaseStartup.open(factory)` turns `DbKeyUnavailable` into `KeyUnavailable(reason)` (`app/.../data/DatabaseStartup.kt:27-32`). `DbKeyUnavailable.Reason` is an enum (`security/DbKeyUnavailable.kt:21`), so it is safe to log.
- `KeyUnavailableScreen(reason)` (`ui/startup/KeyUnavailableScreen.kt:33-58`) has key-specific copy (`res/values/strings.xml:5-8`). Covered by `KeyUnavailableScreenTest` and `MainActivityStartupTest` (`app/src/test/.../MainActivityStartupTest.kt:25-46`, the real fail-closed path under Robolectric, which has no Keystore).
- `DevContent()` is the debug catalogue (`src/debug/.../DevEntry.kt:10-13`) with an empty release twin (`src/release/.../DevEntry.kt:12-15`). `DesignCatalogTest` calls `DevContent()` 7 times (`src/testDebug/.../DesignCatalogTest.kt:47-127`).

**Core contracts (reuse, no change)**
- `IdentityService` (`core/ledger/.../identity/IdentityService.kt:32-132`):
  - `currentIdentity()` returns null before setup and throws `IdentityCorrupt` (`:49-56`);
  - `initialise(userName, deviceLabel)` validates both names with `Validation.requireName(.., LedgerLimits.MAX_NAME_LENGTH)` and writes user, device, setting and three audit rows in one transaction (`:73-85`).
  - Both names throw the **same** `LedgerError.BlankName`/`NameTooLong`, so the service cannot say *which* field failed. The ViewModel must validate each field itself with the same core helper (§3.2 D5).
- `Validation.requireName` (`core/ledger/.../validation/Validation.kt:21-29`) returns the trimmed text. `LedgerLimits.MAX_NAME_LENGTH = 100` (`LedgerLimits.kt:13`).
- `LedgerError` (`validation/LedgerError.kt:16-69`): messages never contain user content, and "ViewModels map them (an exhaustive `when`) to field errors or messages" (`:8-10`).

**Wiring to reuse**
- `RoomTestKit` (`app/src/test/.../testing/RoomTestKit.kt:29-72`) is exactly the services graph A5 needs in production: runner, repositories, `AuditRecorder`, `IdentityService`, `SettingsService` and `AuditedWriter` over a `PftDatabase`, with an injectable `AuditLog` and clock.
  - Its default clock is deliberately **not** millisecond-wrapped, so `RoomAuditedWriterIntegrationTest:240-` can prove that a sub-millisecond clock is refused.
  - `EpochMillis.clock(base)` (`app/.../data/common/EpochMillis.kt:33`) is the wrapper production must use.
- `TestDatabase.create()` (`testing/TestDatabase.kt:17`), `runSuspending` (`testing/RunSuspending.kt:13`) and `HostedComposeRule` (`ui/HostedComposeRule.kt:24`) are required for all Compose JVM tests (M1a `:91`).

**Design system to consume**
- `CollapsingHeaderScaffold(title, style, subtitle, …, actions, bottomBar, …) { LazyListScope }` (`ui/common/CollapsingHeaderScaffold.kt:190-249`).
  - Its `bottomBar` is **already** wrapped in `imePadding()` (`:237`).
  - `BottomActionBar` pads by `safeDrawing` (`BottomActionBar.kt:47-53`).
- `BottomBarAction(label, enabled, onClick)` (`BottomBarAction.kt:10-13`).
- `HeaderIconButton(symbol, label, onClick)` (`HeaderIconButton.kt:38-42`), the only control allowed in header `actions` (DS6e).
- `EmptyState` and `GroupedSection`.
- `PftMotion.navEnter/navExit/navPopEnter/navPopExit` (`ui/theme/PftMotion.kt:100-112`, tested by `NavTransitionTest`).
- `PftSymbol.BACK` and `PftSymbol.SETTINGS` (`PftSymbol.kt:29,45`).

**Lint and scan patterns**
- `config/detekt/detekt.yml:5-12` already has a `ForbiddenImport` list (Material Icons).
- detekt runs **without type resolution** (learnings 2026-10-29 DS4 item 7; design plan `:76`), so `ForbiddenMethodCall` would be inert.
- `ThemeLiteralScanTest` (`app/src/test/.../ui/theme/ThemeLiteralScanTest.kt:26-98`) is a working comment-stripping source scanner. A second scan (logging) makes it a shared test helper.

**Dependencies**
- `gradle/libs.versions.toml` has no navigation, `lifecycle-viewmodel-compose` or explicit coroutines entry.
- The app runtime classpath already resolves lifecycle **2.8.3** (including `lifecycle-runtime-compose` and `lifecycle-viewmodel`), `activity-ktx` 1.9.3 (`by viewModels`) and kotlinx-coroutines **1.7.3**, all transitively (`app/gradle.lockfile`).
- M1a §3.3 pins `navigation-compose` 2.8.4 and lifecycle 2.8.7. kotlinx-serialization (the plugin) is rejected there.

**Manifest:** `app/src/main/AndroidManifest.xml:19-58`. `ManifestHardeningTest` reads application attributes from the merged manifest (`ManifestHardeningTest.kt:51-57`), so it can also pin `enableOnBackInvokedCallback`.

**R8 guard:** `verifyReleaseShrink` (`app/build.gradle.kts:131-213`). Its comment says nothing references `PftDatabase_Impl` "yet" (`:139-140`). A5 makes `PftSymbol` reachable from release code, which DS3 measured at about +87 KB (`app/README.md:276-277`), so the APK is re-measured.

## 3. Design & decision

### 3.1 Startup: a container that opens off the main thread

```
MainActivity.onCreate  (FLAG_SECURE first, unchanged)
  setContent { PftTheme { SyncWindowBackground; Surface(canvas) { PftRoot(app.container) } } }

PftRoot(container)
  LaunchViewModel(container).state : StateFlow<LaunchState>
    Opening           -> nothing drawn (window background = canvas, so no flash)
    KeyUnavailable(r) -> DataUnavailableScreen(code = r.name, key explanation)
    IdentityCorrupt   -> DataUnavailableScreen(code = "IDENTITY_CORRUPT", identity explanation)
    Ready(services, hasIdentity) -> PftNavHost(services, start = if (hasIdentity) HOME else SETUP)
```

- **`di/AppContainer`** (concrete, process-scoped, owned by `PftApplication.container`). It is cheap to construct because nothing opens in the constructor.
  - `suspend fun open(): AppStart` opens the database **once** on an IO dispatcher. It returns `AppStart.Ready(services)` or `AppStart.KeyUnavailable(reason)`, and concurrent callers share the one result.
  - Constructor: `AppContainer(openDatabase: () -> DatabaseStartup, baseClock: Clock = Clock.systemUTC(), io: CoroutineDispatcher = Dispatchers.IO)`. `AppContainer.forApp(app)` passes `{ DatabaseStartup.open(DatabaseFactory.forApp(app)) }`.
  - It builds the services with `EpochMillis.clock(baseClock)`, which meets the A4a criterion.
  - `PftApplication.startup` is **removed** (M1a `:242`). M1b's SMS receiver will call the same `container.open()`.
- **`di/AppServices`** (concrete) is the object graph over an **open** `PftDatabase`: `AppServices(database, clock, auditLog = RoomAuditLog(database.auditDao()))`.
  - It exposes `ids`, `runner`, `identityRepository`, `settingsRepository`, `recorder`, `identity`, `settings` and `writer`, the same set `RoomTestKit` builds today.
  - **`RoomTestKit` is refactored to delegate to it**, so tests exercise the production wiring rather than a copy. The two consumers are the app and the test kit (M1a §3.4 rule).
  - It is a separate class from `AppContainer` because the services cannot exist until the database is open. `AppContainer` owns *when*; `AppServices` owns *what*.
- **`ui/LaunchViewModel`** maps `AppStart` plus `identity.currentIdentity()` to `LaunchState`.
  - `IdentityCorrupt` becomes `LaunchState.IdentityCorrupt` (fail closed, never "new install").
  - It runs its work with `viewModelScope.launch(io)` (an injected dispatcher), so a Robolectric test can await the state from the test thread without blocking the main looper (§10).
- **`DataUnavailableScreen(code, explanation)`** generalises `KeyUnavailableScreen`, which now has two consumers.
  - The title, the "don't clear storage" warning and the `Code: …` line stay shared.
  - Only the explanation paragraph differs. The key copy is unchanged; new copy covers the inconsistent identity.
  - It keeps the A4b rules: no path, no exception text, no reset action.

### 3.2 Key decisions

**D1 — `SafeLog` is a concrete object over typed events.** `log/LogEvent` is a sealed interface whose subclasses carry only `Int`, `Long`, `Boolean`, enums or `java.time.Duration`. `LogEvent.line()` renders `"<snake_name> k=v …"`, and `SafeLog.event(e)` writes `Log.i("pft", e.line())`.
- No method takes a `String`, a `Throwable` or an `Any`, so a message, a name or an amount cannot be passed.
- Events are added **only by the PR that emits them**:
  - A5a: `DbOpened(schemaVersion, took)` and `DbKeyUnavailable(reason)`, from `DatabaseStartup.open`;
  - A5b: `IdentityCorrupt`;
  - A5e: `SetupCompleted`.
- Release builds log too. The events are non-sensitive, and logcat is readable only over adb, which the owner's phone never enables (Q3).
- **Guards:**
  - detekt `ForbiddenImport` adds `android.util.Log`, with a file-level `@Suppress("ForbiddenImport")` and a reason comment in `SafeLog.kt` only (M1a `:147`).
  - `LoggingScanTest` (source scan) also rejects `println(`, `print(`, `System.out`, `System.err`, `printStackTrace(` and fully qualified `android.util.Log.` calls, which `ForbiddenImport` cannot see. It scans `app/src/{main,debug,release}` and `core/*/src/main`.

**D2 — No new abstraction.** `AppContainer`, `AppServices`, `SafeLog`, `LaunchViewModel` and every ViewModel are concrete. `openDatabase: () -> DatabaseStartup` is a function parameter, not an interface, because it is a test seam for "slow open" and "key unavailable". There are no DI frameworks (M1a decision 12).

**D3 — String routes, no type-safe navigation.** Navigation 2.8's type-safe routes need the kotlinx-serialization **plugin**, which M1a §3.3 rejects. Routes are `const val`s in `ui/nav/Routes.kt` (`setup`, `home`, plus debug `dev/catalog`).
- `PftNavHost` passes `PftMotion.navEnter/navExit/navPopEnter/navPopExit` as the four NavHost transitions.
- Setup → Home uses `popUpTo(SETUP) { inclusive = true }`.
- The manifest sets `android:enableOnBackInvokedCallback="true"` (predictive back).

**D4 — `UiState` is a convention, not a type.** M1a §3.4 says "`UiState` convention". A5 documents it in `app/README.md` and follows it; it adds no generic `UiState<T>`, because no A5 screen has a Loading/Content/Error shape with two consumers. The convention:
- one immutable `XxxUiState` data class per screen;
- exposed as `StateFlow` and collected with `collectAsStateWithLifecycle`;
- one-shot outcomes (for example "setup finished") as a state field the screen reacts to, not a `Channel`;
- `LedgerError` mapped in the ViewModel, never in a composable.

B2 may introduce a shared list-state type if its list screens need one.

**D5 — `FieldState` validates with core's own helper.** `ui/common/FieldState.kt`:
- `data class FieldState(text: String = "", error: FieldError? = null)`;
- `sealed interface FieldError { Required; TooLong(max) }` (B1 adds its own cases);
- `fun FieldState.check(rule: (String) -> T): T?` runs a core validation such as `Validation.requireName(it, MAX_NAME_LENGTH)`. It returns the validated value, or null with `error` set, catching only `LedgerError`;
- `LedgerError.toFieldError()` is an exhaustive `when`, so a new `LedgerError` case fails compilation until it is mapped. Non-field errors map to null and are shown as a form-level message.

So the field rules are core's rules (no copy), and the service re-validates as defence in depth.

**D6 — `EditFormScaffold` = `CollapsingHeaderScaffold(HeaderStyle.Standard)` + `BottomActionBar(primary = save)`.**
- Optional `onBack` draws `HeaderIconButton(PftSymbol.BACK, "Back")`; Setup passes none, B2 forms pass one.
- `saving = true` disables the primary action, which guards against a double tap.
- **It adds no `imePadding()`:** the scaffold already applies it to the bottom bar (`CollapsingHeaderScaffold.kt:237`). This corrects the "+ `imePadding`" wording in M1a `:147`, which would double-pad.

**D7 — `NameField`** is a single-line M3 `OutlinedTextField` with:
- a label and `KeyboardCapitalization.Words`;
- `isError` plus supporting text from `FieldError`;
- an IME action (`Next`/`Done`).

It **never truncates or alters** what the user typed; length is checked on Continue. An error clears when that field is edited.

**D8 — Setup copy and defaults.**
- Title "Welcome" with subtitle "Set up this phone".
- One short paragraph: entries are marked with your name and this phone; data stays encrypted on the phone.
- Fields "Your name" (empty) and "This phone's name", prefilled with `Build.MODEL`, editable, trimmed to `MAX_NAME_LENGTH` for the prefill only (Q2).
- Continue validates both fields. If they are valid it calls `identity.initialise(name, label)` and then navigates to Home.
- `AlreadyInitialised` (unreachable in M1a, but possible in a race) is treated as done and navigates to Home. It is not an error, because an identity exists either way.

**D9 — Home is a placeholder, honestly labelled.** `ui/home/HomeScreen`:
- `CollapsingHeaderScaffold(title = "Home", subtitle = "<name> · <device>")`;
- an `EmptyState` ("Accounts and transactions arrive in the next update");
- a debug-only header action that opens the catalogue.

`HomeViewModel` reads `identity.requireIdentity()`. C4 replaces the screen. The placeholder exists only so that Setup has somewhere real to land and the owner can see attribution on A-final.

**D10 — The catalogue becomes a debug route.**
- `src/debug/.../DevEntry.kt` changes from `DevContent()` to `fun NavGraphBuilder.devRoutes()` (adds `dev/catalog` → `DesignCatalog()`) and `@Composable fun DevHeaderActions(navigate: (String) -> Unit)` (one `HeaderIconButton(PftSymbol.SETTINGS, "Design catalogue")`).
- `src/release` twins are empty.
- `DesignCatalogTest` calls `DesignCatalog()` directly.

### 3.3 Dependencies (one pins-only PR, A5c)

Follow `build-logic/README.md` "Refreshing pins" and the learnings of 2026-10-29 (strict build on an empty `GRADLE_USER_HOME`; review the trust-on-first-use checksums).

| Dependency | Pin | Why | Notes |
|---|---|---|---|
| `androidx.navigation:navigation-compose` | 2.8.4 (M1a §3.3) | nav host, predictive back | Pulls `navigation-runtime/common` and `kotlinx-serialization-core` at **runtime** [Verify at install]. The serialization *plugin* is not applied (D3) |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.8.7 (M1a §3.3) | `viewModel(factory)` in composables | Moves the whole lifecycle family 2.8.3 → 2.8.7 |
| `androidx.lifecycle:lifecycle-runtime-compose` | 2.8.7 | `collectAsStateWithLifecycle` | Already resolved transitively at 2.8.3; declared because app code now uses it directly |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | whatever resolution already selects after the two above (expected 1.7.3) [Verify] | `Dispatchers`, `StateFlow`, `viewModelScope` used directly | Declared, not bumped. M1a §3.3's 1.9.0 is not needed for A5, and core still has no coroutines dependency (M1a A3 note `:127`) |

**Not added:**
- `navigation-testing` (tests use the real host and assert on screens);
- `kotlinx-coroutines-test` (tests use real dispatchers, `runSuspending` and the compose rule's idling);
- `ui-test-manifest` (banned since DS1).

## 4. Data model & schema

**No schema change.** Setup writes the existing v1 tables (`user_profile`, `device`, `app_setting`, `audit_entry`) through the existing `IdentityService`. `PftDatabase.VERSION` stays 1.

## 5. Flows

**Cold start, fresh install**
1. `MainActivity.onCreate` sets `FLAG_SECURE`, enables edge-to-edge and calls `setContent`. `PftRoot` obtains `LaunchViewModel` with `viewModel(factory)`; its state is `Opening`, and only the canvas is drawn.
2. On IO, `AppContainer.open()` runs `DatabaseStartup.open(DatabaseFactory.forApp(app))`. That is the A4b order: key, backup, SQLCipher open, `recordOpened`.
3. `SafeLog` writes `db_opened schema=1 ms=<n>`, and the container builds `AppServices(db, EpochMillis.clock())`.
4. `identity.currentIdentity()` returns null, so the state is `Ready(services, hasIdentity = false)` and `PftNavHost` starts at `setup`.
5. The user types a name and a phone name and taps Continue:
   - `SetupViewModel.submit()` validates each `FieldState` with `Validation.requireName`;
   - it sets `saving = true` and calls `identity.initialise(name, label)`;
   - that runs `RoomTransactionRunner.inTransaction { insert user; insert device; put setting; 3 × audit }` against SQLCipher;
   - `SafeLog` writes `setup_completed`, and the state's `done` flag is set.
6. The screen observes `done` and navigates `home`, popping `setup`. `HomeViewModel` reads the identity, and the header subtitle reads "Akash · Pixel 8".

**Later starts:** steps 1–3 run, then `hasIdentity = true` and the host starts at `home`.

**Fail closed**
- Key unavailable: `SafeLog` writes `db_key_unavailable reason=KEK_MISSING`, and the screen shows `Code: KEK_MISSING`. No file is created (as A4b).
- `IdentityCorrupt`: `SafeLog` writes `identity_corrupt`, and the screen shows `Code: IDENTITY_CORRUPT`. No write happens.

## 6. Implementation steps (TDD-ordered)

Conventions: app JVM tests in `app/src/test/kotlin/dev/imagh/pft/app/…`; Robolectric tests use `@RunWith(RobolectricTestRunner::class) @Config(sdk = [35])`; Compose tests use `HostedComposeRule`, with one `setContent` per test (learnings DS5 item 5); goldens use Roborazzi `@GraphicsMode(NATIVE)` at `w393dp-h851dp-xxhdpi`. Every step ends with a green `./gradlew check`.

### Step 1 — `LogEvent` rendering (A5a)
- **Red:** `log/LogEventTest` (JUnit 5, pure):
  - `LogEvent.DbOpened(schemaVersion = 1, took = 12.ms).line() == "db_opened schema=1 ms=12"`;
  - `LogEvent.DbKeyUnavailable(Reason.KEK_MISSING).line() == "db_key_unavailable reason=KEK_MISSING"`;
  - a 0 ms and a > 60 s duration render as integer milliseconds.
- **Green:** `app/src/main/kotlin/dev/imagh/pft/app/log/LogEvent.kt`.
- **Refactor:** none.

### Step 2 — `LogEvent` can hold no text (A5a)
- **Red:** `log/LogEventShapeTest` (JUnit 5).
  - A list holds one sample of every subclass. A private `fun covered(e: LogEvent) = when (e) { … }` is exhaustive with no `else`, so adding a subclass without a sample fails **compilation**.
  - For each sample, every non-static, non-synthetic declared field (Java reflection, no kotlin-reflect) has a type in {`int`, `long`, `boolean`, an `Enum` subclass, `java.time.Duration`}.
  - The test also proves the check can fail: a local test-only class with a `String` field is rejected by the same predicate.
- **Green:** none expected, since Step 1's events comply. If a field type fails, fix the event.
- **Refactor:** none.

### Step 3 — `SafeLog` writes the line, and nothing else can log (A5a)
- **Red:**
  - `log/SafeLogTest` (Robolectric): `SafeLog.event(DbOpened(1, 12.ms))` produces exactly one `ShadowLog` entry with tag `pft`, priority INFO and message `db_opened schema=1 ms=12`.
  - `LoggingScanTest` (JUnit 5):
    - fixture lines with `println("x")`, `System.out.print`, `e.printStackTrace()` and `android.util.Log.d(…)` are each flagged, while the same tokens inside comments are not;
    - the real scan of `app/src/{main,debug,release}/kotlin` and `../core/*/src/main/kotlin` finds no hit outside `log/SafeLog.kt`;
    - the scan asserts it read more than 10 files, so it is looking at the tree.
- **Green:**
  - `log/SafeLog.kt` (`object SafeLog { fun event(e: LogEvent) }`, `TAG = "pft"`, `@file:Suppress("ForbiddenImport")` with the reason);
  - `config/detekt/detekt.yml` adds `'android.util.Log'` to `ForbiddenImport.imports`, with a comment pointing at `SafeLog`.
- **Refactor:**
  - extract `stripComments`, the per-line regex scan and the root walk from `ThemeLiteralScanTest` into `app/src/test/.../testing/SourceScan.kt`;
  - make `ThemeLiteralScanTest` use it, with its existing tests unchanged and green; this gives two consumers;
  - confirm by hand (not committed) that a scratch `import android.util.Log` elsewhere fails `./gradlew detekt`.

### Step 4 — The database open is logged (A5a)
- **Red:** extend `data/DatabaseStartupTest`:
  - a `Ready` open logs `db_opened schema=1 ms=<digits>`;
  - a `KeyUnavailable` open logs `db_key_unavailable reason=<code>`;
  - no log line contains the database path, the file name or the word "key" followed by bytes. The assertion is that the whole message matches `^[a-z_]+( [a-z_]+=[A-Z0-9_]+)*$`.
- **Green:** `DatabaseStartup.open` times `factory.open()` with `System.nanoTime()` and calls `SafeLog.event` in both branches.
- **Refactor:** none.

### Step 5 — `AppServices`: the production object graph (A5b)
- **Red:** `di/AppServicesTest` (Robolectric, real `TestDatabase`):
  - `AppServices(db, EpochMillis.clock(testClock))`, with the `TestClock` set to `…T10:00:00.123456789Z`;
  - `identity.initialise("Akash", "Pixel")` succeeds;
  - `currentIdentity()` re-reads the same identity;
  - the three audit rows' `at` equal `…T10:00:00.123Z` (the A4a criterion: written equals read, no `IllegalArgumentException`).
- **Green:** `app/src/main/kotlin/dev/imagh/pft/app/di/AppServices.kt`.
- **Refactor:**
  - `RoomTestKit` builds `AppServices(database, wrapClock(clock), auditLog)` and forwards its fields;
  - its public surface (`clock`, `ids`, `runner`, `identityRepository`, `settingsRepository`, `recorder`, `identity`, `settings`, `writer`, `initialise()`, `trailOf()`) is unchanged;
  - `IdentityRoomIntegrationTest`, `RoomAuditedWriterIntegrationTest` (including its sub-millisecond refusal test) and the other Room tests stay green **unmodified**.

### Step 6 — `AppContainer` opens once, off the caller's thread (A5b)
- **Red:** `di/AppContainerTest` (Robolectric):
  1. *Off the main thread:* `openDatabase` records `Thread.currentThread()` and returns `Ready(TestDatabase.create())`. Calling `open()` from the test (main) thread with `runSuspending` gives `AppStart.Ready`, and the recorded thread is not the caller's.
  2. *Not blocking:* with an `openDatabase` that blocks on a latch, a coroutine calling `open()` is still suspended while the test thread keeps running and later releases the latch. This is the "slow fake factory" that M1a `:242` asks for.
  3. *Opened once:* two concurrent `open()` calls invoke `openDatabase` exactly once (an invocation counter on the lambda, an observable count of opens) and both receive the same `AppServices` instance.
  4. *Fail closed:* an opener returning `KeyUnavailable(KEK_MISSING)` gives `AppStart.KeyUnavailable(KEK_MISSING)`, and no `AppServices` is built.
  5. *Millisecond clock:* `AppContainer(opener, baseClock = testClock at a sub-millisecond instant)` then `open()`, then `identity.initialise(…)` succeeds and stores a whole-millisecond `at`.
- **Green:**
  - `di/AppContainer.kt` and `di/AppStart.kt`;
  - `PftApplication`: replace `startup` with `val container: AppContainer by lazy { AppContainer.forApp(this) }`.
- **Refactor:** none. `MainActivity` still compiles against `startup` until Step 8, so Steps 6–8 land in the same PR (A5b).

### Step 7 — `LaunchViewModel` decides the route (A5b)
- **Red:** `ui/LaunchViewModelTest` (Robolectric, real Room). Each case awaits the first non-`Opening` state with `runSuspending { vm.state.first { it != Opening } }`:
  - fresh DB → `Ready(hasIdentity = false)`;
  - after `services.identity.initialise(…)` → `Ready(hasIdentity = true)`;
  - a `current_device_id` row pointing nowhere (written through `settingsRepository.put`, as in `IdentityRoomIntegrationTest:101`) → `IdentityCorrupt`, and the log has `identity_corrupt`;
  - opener `KeyUnavailable` → `KeyUnavailable(reason)`.
- **Green:**
  - `ui/LaunchViewModel.kt` with `LaunchState` and a `factory(container, io)`;
  - `LogEvent.IdentityCorrupt`, with a sample added in `LogEventShapeTest`.
- **Refactor:** none.

### Step 8 — `PftRoot`, the generalised fail-closed screen and `MainActivity` (A5b)
- **Red:**
  - Rename `KeyUnavailableScreenTest` to `DataUnavailableScreenTest`:
    - the key variant still shows the A4b title, explanation, warning and `Code: KEK_MISSING`;
    - the identity variant shows the same title and warning, the new explanation and `Code: IDENTITY_CORRUPT`;
    - neither shows anything but fixed strings.
  - `ui/PftRootTest` (`HostedComposeRule`), given a container over `TestDatabase`:
    - an inconsistent identity shows `Code: IDENTITY_CORRUPT`;
    - a ready, initialised database shows `DevContent` for now. Until Step 14 swaps in the nav host, assert the catalogue's first section title.
  - `MainActivityStartupTest`: both existing tests still pass, now waiting with `compose.waitUntil { … }` because the open is asynchronous. A new assertion checks that the activity's first composition happens before the opener runs to completion (Opening shows no text nodes).
- **Green:**
  - `ui/startup/DataUnavailableScreen.kt` (renamed from `KeyUnavailableScreen.kt`) with a `@StringRes explanation` and a `code: String`;
  - a new string `identity_corrupt_explanation`;
  - `ui/PftRoot.kt`;
  - `MainActivity` calls `PftRoot(app.container)`.
- **Refactor:** delete the "A5 moves it off the main thread" notes from `PftApplication`/`DatabaseFactory` KDoc, and update the `verifyReleaseShrink` comment at `app/build.gradle.kts:139-140`.

### Step 9 — Dependencies (A5c, pins only; merges before A5b, so do it first in practice)
- **Red:** no behaviour. The strict build on an empty `GRADLE_USER_HOME` fails verification until the metadata is written; that failure is the red.
- **Green:**
  - `libs.versions.toml` entries per §3.3 and `app/build.gradle.kts` `implementation(...)` lines;
  - `app/gradle.lockfile` and `gradle/verification-metadata.xml` regenerated per `build-logic/README.md` "Refreshing pins";
  - `./gradlew check assembleRelease assembleDebug` strict on an empty cache;
  - `dependencyCheckAggregate` run, with its result recorded in the PR.
- **Refactor:** none.

### Step 10 — `FieldState` and `FieldError` (A5d)
- **Red:** `ui/common/FieldStateTest` (JUnit 5, pure, real `Validation`). With `rule = { Validation.requireName(it, 100) }`:
  - `FieldState("  ").check(rule)` gives null with `error = Required`;
  - 101 letters gives null with `TooLong(100)`;
  - `" Akash "` gives `"Akash"` with `error = null`;
  - `edit("A")` clears the error;
  - `LedgerError.toFieldError()`: `BlankName` → `Required`, `NameTooLong(100)` → `TooLong(100)`, and `NotInitialised`/`IdentityCorrupt` → null (form-level).
- **Green:** `ui/common/FieldState.kt`, `FieldError.kt` and `LedgerErrorMapping.kt` (an exhaustive `when` over `LedgerError`).
- **Refactor:** none.

### Step 11 — `NameField` and `EditFormScaffold` (A5d)
- **Red:**
  - `ui/common/NameFieldTest`:
    - the label is shown;
    - typing 120 characters keeps all 120 (no truncation, read back from the node's `EditableText`);
    - with `error = TooLong(100)` the supporting text "At most 100 characters" is displayed and the node has error semantics;
    - with `error = Required` it shows "Required".
  - `ui/common/EditFormScaffoldTest`:
    - the primary label ("Save" or the caller's) is inside the bottom bar (the node is a descendant of the bar, matched by its `Role.Button` and position below the list's bounds);
    - `saving = true` makes it not enabled;
    - `onBack = null` means no "Back" button, and with `onBack` a "Back" button exists whose touch bounds are at least 48dp (`touchBoundsInRoot`, learnings DS6e);
    - content items render in the list.
  - Goldens: `FormScreenshotTest` renders a sample form (two `NameField`s, one in error) in light, dark and 200% font scale.
- **Green:** `ui/common/NameField.kt`, `ui/common/EditFormScaffold.kt`, `ui/common/FormSamples.kt` (fake-data previews and samples), and strings `field_required` and `field_too_long`.
- **Refactor:**
  - add `FormSamples` to `mustBeRemoved` in `verifyReleaseShrink`;
  - add a "Forms" page to `DesignCatalog` (debug), with `DesignCatalogTest` asserting the page exists.

### Step 12 — `SetupViewModel` (A5e)
- **Red:** `ui/setup/SetupViewModelTest` (Robolectric, real Room through `AppServices`; the main looper is idled by a small helper, §10):
  - a blank name gives `name.error = Required`, nothing is persisted (`users()` empty, zero audit rows) and `done == false`;
  - a valid name with a blank phone name puts the error on the **label** field only;
  - a valid submit sets `done = true`, `currentIdentity()` returns that user and device (trimmed), and the log has `setup_completed`;
  - two rapid `submit()` calls create one user and three audit rows, and no error is shown (the `saving` guard);
  - the label is prefilled with the injected `defaultDeviceLabel`, trimmed to 100.
- **Green:** `ui/setup/SetupViewModel.kt` (with `SetupUiState`) and `LogEvent.SetupCompleted`, with its sample.
- **Refactor:** none.

### Step 13 — `SetupScreen` (A5e)
- **Red:**
  - `ui/setup/SetupScreenTest` (stateless screen with fake state):
    - title "Welcome", both fields and the explanatory text are shown;
    - **Continue is inside the bottom bar** (the design-system amendment);
    - an error state shows "Required" under "Your name".
  - Goldens: `SetupScreenshotTest` in light, dark and 200% font scale, with the header expanded. These are the owner's first look before A-final (design plan `:966`).
- **Green:** `ui/setup/SetupScreen.kt` (a stateless `SetupScreen(state, onNameChange, onLabelChange, onContinue)` plus a `SetupRoute` that wires the ViewModel) and its strings.
- **Refactor:** none.

### Step 14 — `PftNavHost`, Home placeholder, debug route, predictive back (A5f)
- **Red:**
  - `ui/nav/SetupFlowTest` (Robolectric, `HostedComposeRule`, real `AppContainer` over `TestDatabase`, rendered through `PftRoot`; this is the M1a acceptance test):
    - a fresh DB shows Setup;
    - enter "Akash" and "Pixel 8", tap Continue, and Home appears with subtitle "Akash · Pixel 8";
    - the DB holds one user, one device, `current_device_id` and three audit rows;
    - pressing system back on Home finishes the activity (`activity.isFinishing`), so Setup is not on the back stack.
  - `ui/nav/LaunchRoutingTest`: an already initialised DB opens on Home, with no Setup node ever composed.
  - `ui/home/HomeViewModelTest`: the state carries the user name and device label from the identity.
  - `testDebug/DevRouteTest`: on Home, "Design catalogue" opens the catalogue.
  - `testRelease/ReleaseHasNoCatalogueTest` (new source set): Home has no "Design catalogue" action.
  - `ManifestHardeningTest`: the merged manifest's `<application>` has `enableOnBackInvokedCallback="true"`.
  - `DesignCatalogTest`: switch `DevContent()` to `DesignCatalog()`; same assertions.
- **Green:**
  - `ui/nav/{PftNavHost,Routes}.kt`, `ui/home/{HomeScreen,HomeViewModel}.kt`;
  - debug and release `DevEntry.kt` changed to `devRoutes()` and `DevHeaderActions()`;
  - `PftRoot` shows `PftNavHost` for `Ready`;
  - the manifest attribute.
- **Refactor:** remove `DevContent`; update the `PftRootTest` "ready" case from Step 8 to expect Home or Setup instead of the catalogue.

### PR breakdown (incremental — required)

**Target branch.** A5 is a step inside the existing base **`feat/m1a-foundation`**. Every A5 PR targets that base; A-final (foundation → `main`) follows A5f, as M1a already plans. Slice branches use the learned flat naming (learnings 2026-10-28: a ref cannot nest under the base name), each cut in its **own worktree** (owner rule 2026-10-29).

| PR | Branch | Scope (steps) | Target | Depends on | Why it's independently reviewable |
|----|--------|---------------|--------|------------|-----------------------------------|
| A5a | `feat/m1a-foundation-a5a-safelog` | 1–4: `LogEvent`, `SafeLog`, detekt ban, `LoggingScanTest` + shared `SourceScan`, DB-open events | `feat/m1a-foundation` | — | One concern: the logging policy and its guards. No UI, no dependencies |
| A5c | `feat/m1a-foundation-a5c-nav-lifecycle-pins` | 9: navigation-compose, lifecycle 2.8.7, explicit coroutines; locks + checksums | `feat/m1a-foundation` | — (parallel with A5a) | Pins only: the reviewer checks trust-on-first-use checksums and the transitive set without code noise. Lands before A5b so app code never uses an undeclared transitive artifact (`lifecycle-runtime-compose`, coroutines) |
| A5b | `feat/m1a-foundation-a5b-app-container` | 5–8: `AppServices` (+ `RoomTestKit` delegation), `AppContainer`, `LaunchViewModel`, `PftRoot`, `DataUnavailableScreen`, `PftApplication.startup` retired | `feat/m1a-foundation` | A5a (logs `identity_corrupt`), A5c (declared lifecycle and coroutines) | Startup moves off the main thread, with no visible change except the identity-corrupt screen |
| A5d | `feat/m1a-foundation-a5d-form-blocks` | 10–11: `FieldState`/`FieldError`/`LedgerError` mapping, `NameField`, `EditFormScaffold`, samples, goldens, catalogue page | `feat/m1a-foundation` | — (parallel) | Shared UI blocks with named consumers (Setup in A5e, B2+ forms), testable on their own |
| A5e | `feat/m1a-foundation-a5e-setup-screen` | 12–13: `SetupViewModel`, `SetupScreen`, goldens, `setup_completed` event | `feat/m1a-foundation` | A5b, A5c, A5d | One screen and its ViewModel, not yet reachable |
| A5f | `feat/m1a-foundation-a5f-nav-host` | 14: `PftNavHost`, Home placeholder, debug catalogue route, predictive back, `SetupFlowTest`; feature doc `first-run-and-profiles.md` | `feat/m1a-foundation` | A5e | Wires the flow end to end; small because every piece already exists |
| A-final | (existing) | foundation → `main` | `main` | A1–A5f, DS1–DS6 | As M1a |

Each PR carries its own KDoc, README, learnings and security-review section, and runs `graphify update .` when structure changes.

## 7. Test plan

**Unit (JUnit 5, pure, fast)**
- `LogEventTest`: format.
- `LogEventShapeTest`: no text-bearing fields; adding an unsampled subclass is a compile error.
- `LoggingScanTest`: forbidden calls; the scanner is proven on fixtures first.
- `FieldStateTest`: validation through the real core `Validation`, error mapping and error clearing.
- `ThemeLiteralScanTest`: unchanged behaviour after the `SourceScan` extraction.

**Integration (Robolectric, real in-memory Room, no mocked DAO)**
- `DatabaseStartupTest`: log lines on both outcomes, and that they carry nothing else.
- `AppServicesTest`: production wiring; written equals read on a millisecond clock.
- `AppContainerTest`: off-thread, non-blocking, opened once, fail closed, millisecond clock.
- `LaunchViewModelTest`: the four launch states.
- `SetupViewModelTest`: per-field errors, nothing persisted on error, the double-submit guard, persisted identity and three audit rows.
- `HomeViewModelTest`.
- The existing `IdentityRoomIntegrationTest` and `RoomAuditedWriterIntegrationTest`, unmodified, now running through `AppServices` via `RoomTestKit`.

**UI / flow (Robolectric Compose, `HostedComposeRule`)**
- `NameFieldTest`, `EditFormScaffoldTest`, `SetupScreenTest`, `DataUnavailableScreenTest`, `PftRootTest`.
- `SetupFlowTest` and `LaunchRoutingTest`, end to end through `PftRoot` → `AppContainer` → Room.
- `MainActivityStartupTest`: the real Application; the Keystore is absent under Robolectric, so it fails closed asynchronously.
- `DevRouteTest` (debug) and `ReleaseHasNoCatalogueTest` (release).
- `ManifestHardeningTest`: predictive back flag, and still only the launcher exported.

**Screenshots (Roborazzi):** `FormScreenshotTest` and `SetupScreenshotTest` in light, dark and 200% font scale.

**Instrumented:** none new. The SQLCipher and Keystore paths are unchanged; the emulator walk-through is §11.

**Edge and failure cases:**
- blank, whitespace-only and 101-character names, and the same for the label;
- double tap on Continue;
- `AlreadyInitialised` race (D8);
- an `IdentityCorrupt` database;
- key unavailable;
- a slow open (no main-thread block);
- concurrent `open()`;
- a sub-millisecond base clock;
- back from Home;
- 200% font scale on the Setup screen;
- IME shown (Continue stays above it; the existing `BottomActionBar` inset test covers the mechanism).

**AC → test map**

| AC | Tests |
|---|---|
| L-8 (profile per device on first run; writes attributed) | `SetupFlowTest`, `SetupViewModelTest`, `IdentityRoomIntegrationTest`, `AppServicesTest` |
| SEC-5 (no financial content in logs) | `LogEventShapeTest`, `LoggingScanTest`, `SafeLogTest`, `DatabaseStartupTest` log assertions, detekt `ForbiddenImport` |
| SEC-2 (`FLAG_SECURE` on every screen) | `MainActivityStartupTest` (one activity hosts every screen, including Setup and Home) |
| SEC-3 / SEC-6 (no permissions; only the launcher exported) | `ManifestHardeningTest` (re-run with the nav and lifecycle deps merged in) |
| INT time precision (A4a criterion) | `AppServicesTest`, `AppContainerTest` case 5 |
| NFR responsiveness (no open on the UI thread; A4b follow-up) | `AppContainerTest` cases 1–2, `MainActivityStartupTest` first-frame assertion |
| Fail closed on corrupt identity | `LaunchViewModelTest`, `PftRootTest`, `DataUnavailableScreenTest` |
| Design-system amendment (Continue in bottom bar; nav transitions; predictive back; catalogue debug-only) | `SetupScreenTest`, `EditFormScaffoldTest`, `ManifestHardeningTest`, `DevRouteTest`, `ReleaseHasNoCatalogueTest`, §11 manual predictive-back check |

## 8. Security considerations

- **Untrusted input:** the two Setup fields. They are validated with core's `Validation.requireName` in the ViewModel (field attribution) **and** again in `IdentityService` (defence in depth). Input is never truncated or altered silently. Room's parameterised DAOs store it; there is no SQL string building.
- **Logs (SEC-5, CLAUDE.md §3.7):**
  - the type system prevents text in `LogEvent`;
  - detekt bans `android.util.Log` imports;
  - a source scan bans `println`, `System.out/err`, `printStackTrace` and fully qualified `Log` calls across `app` and `core`.
  - Names typed on Setup never reach a log. `setup_completed` has no fields.
  - `LedgerError` messages never reach a log (`SafeLog` takes no strings).
- **Fail closed:**
  - key unavailable (unchanged);
  - an inconsistent identity becomes a blocking screen, never a fresh identity over existing data;
  - an uncaught startup failure (backup failed, SQLCipher open failed) still ends the process without touching data, exactly as A4b. Moving it into a coroutine does not swallow it, because `viewModelScope` has no handler. A test asserts that a throwing opener propagates (`AppContainerTest`: `open()` rethrows and nothing is cached as Ready).
- **Error surfaces:** `DataUnavailableScreen` shows only fixed copy and a fixed code. There is no exception text, path or name.
- **Exported surface:** unchanged. Only `MainActivity` is exported. The nav graph has **no deep links** (`ManifestHardeningTest` would catch an added intent filter). `enableOnBackInvokedCallback` is not a security attribute.
- **Debug-only tooling:** the catalogue route and its header action are compiled into debug only (`src/debug`). `ReleaseHasNoCatalogueTest` and the release `DevEntry` twin keep them out of release.
- **Dependencies:** three new pins plus an explicit coroutines declaration, locked and checksummed. `dependencyCheckAggregate` is run in A5c and its result recorded. navigation-compose brings `kotlinx-serialization-core` at runtime; the scan covers it. No analytics, crash or network SDKs; still no `INTERNET`.
- **Secrets:** none touched. The DB key handling is unchanged (A4b). `AppContainer` never sees key bytes.
- Run `/security-review` and `/review-changes` before every PR (TECHNICAL.md §5). A5b and A5f matter most, because they change the startup and the exported activity's content.

## 9. Documentation updates (all required surfaces)

| Surface | Change | PR |
|---|---|---|
| Inline KDoc | every new public class and function: `LogEvent`, `SafeLog`, `AppServices`, `AppContainer`, `AppStart`, `LaunchViewModel`/`LaunchState`, `PftRoot`, `DataUnavailableScreen`, `FieldState`/`FieldError`, `NameField`, `EditFormScaffold`, `SetupViewModel`/`SetupScreen`, `PftNavHost`/`Routes`, `HomeScreen`/`HomeViewModel`, `devRoutes`/`DevHeaderActions` | each |
| `app/README.md` | **Status** line. **Public interface:** new `log`, `di`, `ui/nav`, `ui/setup`, `ui/home` sections, and the `ui/common` form-block rows. **Startup:** `PftApplication.container`, off-main open, launch states; remove the "until A5" notes at `:10-16`, `:172`, `:194`, `:206`, `:216-217`. **UiState convention** (D4). **Logging policy** (`SafeLog` only, detekt and scan guards). **Dependencies** (nav, lifecycle 2.8.7, coroutines). **Tests** list. **Release APK size** re-measured | A5a, A5b, A5c, A5d, A5e, A5f |
| `docs/features/first-run-and-profiles.md` (new) | what Setup asks and why (attribution, L-8); what happens on later launches; that each phone has its own profile until sync (M1c/M2); the placeholder Home; that names stay on the phone and are never logged | A5f (draft in A5e) |
| `docs/features/data-protection.md` | the screen now also covers `IDENTITY_CORRUPT`: what it means and what to do; codes table | A5b |
| `docs/architecture/overview.md` | status; startup sequence (container, launch states, nav host); call flow example Setup → ViewModel → `IdentityService` → Room | A5b, A5f |
| `docs/architecture/persistence-and-security.md` | open off the main thread via `AppContainer`, opened once and shared by future entry points (M1b receiver); fail-closed table gains the identity row; logging policy | A5a, A5b |
| `docs/architecture/code-structure.md` | layout gains `log/`, `di/`, `ui/nav/`, `ui/setup/`, `ui/home/`, `ui/PftRoot`, `ui/startup/DataUnavailableScreen`, `src/testRelease/`; the shared-blocks table gains `AppServices` (app + `RoomTestKit`), `FieldState`/`FieldError` + `LedgerError` mapping, `NameField`, `EditFormScaffold` (consumers: Setup, B2 account edit, B3/B5 forms, D/E/F/G edit screens) and `SourceScan` (test: theme scan, logging scan); fix `:132` | A5a, A5b, A5d, A5f |
| `docs/architecture/design-system.md` | nav transitions wired (`:234-235`), the predictive-back result from §11, `EditFormScaffold` (no extra IME padding, D6) | A5d, A5f |
| `config/detekt/detekt.yml` comment | why `android.util.Log` is banned and where the one exception lives | A5a |
| `core/ledger/README.md` | **no change** (no core code changes); confirm in review | — |
| `docs/plans/2026-10-28-m1a-core-ledger.md` | an "As built (A5)" note: the split into A5a–f, `PftApplication.startup` retired, the `UiState`-as-convention reading (D4), the `imePadding` correction (D6), string routes (D3), coroutines declared at the resolved version (§3.3) | A5f |
| `docs/learnings/README.md` | one entry per PR. Expected topics: Robolectric main-looper waits for ViewModels; navigation 2.8 transitive serialization; predictive-back result; release APK delta once `PftSymbol` is reachable; any detekt `@file:Suppress` behaviour | each |
| Root `README.md` | status line (first-run setup exists; the app opens to Setup) | A5f |
| `graphify-out/` | `graphify update .` | each PR that adds files |

## 10. Risks & mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| ViewModel coroutines dispatched to `Main` deadlock when a Robolectric test blocks the main thread with `runBlocking` | hanging or flaky tests | `LaunchViewModel` launches its work on an injected IO dispatcher. Screen-level ViewModels are tested through the compose rule (which idles the looper) or a `testing/awaitMain { predicate }` helper that loops `shadowOf(getMainLooper()).idle()` with a timeout. Record in learnings |
| navigation-compose 2.8.4 pulls a larger transitive set (serialization-core, navigation-common) and bumps lifecycle 2.8.3 → 2.8.7 | checksum/TOFU review burden; a version conflict | isolated pins-only PR A5c; strict build on an empty `GRADLE_USER_HOME`; OWASP scan; `ManifestHardeningTest` catches any merged-manifest additions |
| Predictive back on API 35 with nav-compose 2.8.4 shows a glitch or no animation | cosmetic | the flag keeps system back working regardless; verify on a throwaway AVD (§11). If broken, keep the flag and note it in learnings and the design-system doc rather than block A5 |
| R8 strips something navigation or lifecycle reflect on, or the APK grows | release crash or size creep | libraries ship consumer rules; `verifyReleaseShrink` still asserts the keep list; emulator release smoke (§11); record the APK delta (`PftSymbol` now reachable, about +87 KB expected) |
| `MainActivityStartupTest` becomes timing-sensitive after the async open | flaky gate | use `compose.waitUntil(timeoutMillis = 5_000) { … }` on the visible text, never sleeps |
| A long first open (migration in later slices) shows only the canvas | looks frozen | acceptable for A5 (v1, no migrations); `db_opened ms=` gives real numbers on the emulator. If over about 500 ms, a later slice adds a progress indicator |
| `ForbiddenImport` file-level suppression not honoured by detekt 1.23.7 | gate passes SafeLog falsely or blocks it | [Verify] in A5a; fallback is the rule's `excludes: ['**/log/SafeLog.kt']`. The scan test is the second line either way |
| Typed Setup text is lost on process death | user retypes two short fields | accepted (out of scope); revisit if Setup grows |

## 11. Rollout & verification

**Per PR:** `./gradlew check` (unit, Robolectric, goldens, coverage floor on core, ktlint, detekt, lint, `verifyReleaseShrink`). A5c also runs the strict empty-cache build and `dependencyCheckAggregate`.

**End to end (A5f; on a throwaway AVD per the emulator note if `pft35` is busy):**
1. `./gradlew :app:installDebug` on a clean emulator, then launch.
   - Setup shows the "Welcome" header and the two fields; the phone name is prefilled.
   - Verify with `uiautomator dump`, because `FLAG_SECURE` blanks screencap.
2. Tap Continue with an empty name: "Required" shows under "Your name".
3. Enter a name and tap Continue: Home shows "<name> · <phone>".
4. `adb logcat -s pft`:
   - only lines matching `^[a-z_]+( [a-z_]+=[A-Z0-9_]+)*$`;
   - `db_opened schema=1 ms=<n>` and `setup_completed`;
   - the typed name appears nowhere: `adb logcat -d | grep -c <name>` returns 0.
5. Back on Home exits the app. Relaunch: straight to Home.
6. Predictive back: from Home, open the debug catalogue (header action), then perform a slow back swipe. The catalogue previews the pop with the `PftMotion` transition. Record the result in learnings and `design-system.md`.
7. Rotate on Setup with text typed: the text survives (ViewModel).
8. `adb shell run-as dev.imagh.pft ls databases` shows `pft.db`, and `head -c 16` is not the SQLite header (unchanged A4b check).
9. Release smoke:
   - `./gradlew :app:assembleRelease`;
   - sign a copy with the debug key (learnings 2026-10-30 recipe);
   - install on the emulator, complete Setup and relaunch;
   - record the APK size delta.

**Rollback:** each A5 PR is revertable on its own inside `feat/m1a-foundation`, in reverse order. There is no schema change, so no data migration is involved. A database initialised by A5 remains valid for any later build, because Setup writes only v1 tables through the existing service.

## 12. Open questions

**Resolved 2026-11-01:** the owner accepted every default below. Q1's existing-user picker is tracked on the PRD roadmap (§6.2, R-1) so it is not lost.

| # | Question | Default |
|---|---|---|
| Q1 | Should Setup also offer **"I'm already a household member"** (pick an existing user, `initialiseForExistingUser`)? In M1a no path creates users without a device: each phone is set up fresh, and the profiles meet only when sync arrives. | **Defer** to the restore or sync slice that makes it reachable (YAGNI). A5 builds only the new-user path; the core method already exists |
| Q2 | Prefill "This phone's name" with the device model (`Build.MODEL`, for example "Pixel 8")? It is stored in the encrypted database and in the audit trail, never logged. | **Yes**, editable |
| Q3 | Should `SafeLog` also write in **release** builds? The events carry no text, and logcat is reachable only over adb, which the owner's phone never enables. | **Yes**, the same events in both builds. They help diagnose the emulator release smoke and M1b receiver issues |
| Q4 | Ship A5 as **six PRs** (A5a–A5f) rather than M1a's single A5 PR? | **Six**, per CLAUDE.md §6 sizing. Merge order A5a, A5c, A5b, A5d, A5e, A5f; A5a, A5c and A5d can be reviewed in parallel |

**Follow-ups recorded during A5b (not decisions; no code change in A5):**

| # | Follow-up | Where it lands |
|---|---|---|
| F1 | `AppContainer` caches `KeyUnavailable` for the process lifetime. M1b's SMS receiver may trigger the first open at boot, before the Keystore is usable: consider re-attempting only on the `KeyUnavailable` value, keeping "never retry" for thrown failures (no second database over a failed first attempt). | M1b |
| F2 | An unexpected exception from `container.open()` or `identity.currentIdentity()` inside `LaunchViewModel` is not caught, so it crashes every launch while the cause persists (fails closed, as documented in §8). Decide whether the user should see a screen instead, and with which fixed code. | A5f or H2 (diagnostics) |
