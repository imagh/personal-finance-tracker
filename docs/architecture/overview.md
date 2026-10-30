# Architecture overview

How Personal Finance Tracker is put together. Status: **M1a, slice A4b** (the Room implementations of the core boundary, encrypted with SQLCipher
under a Keystore-wrapped key, on top of the A3 ledger foundations, the A1 skeleton and the DS tokens;
see [persistence-and-security.md](persistence-and-security.md)). This file is refined as each slice lands
(next: A5, the app container and first-run setup). Rules live in
`CLAUDE.md` §3 and `docs/TECHNICAL.md` §6; the M1a plan is
`docs/plans/2026-10-28-m1a-core-ledger.md`.

## Shape

A single Android application with no server. Business logic lives in pure Kotlin/JVM `core`
modules that have **no Android imports**, so they build and test on a plain JVM in seconds; the
`app` module is the thin Android layer on top.

```
                 +-----------------------------+
                 |  :app  (Android)            |  Compose UI, ViewModels, Room/SQLCipher
                 |  dev.imagh.pft.app          |  repositories, Keystore, app lock, SafeLog
                 +--------------+--------------+
                                | depends on (never the reverse)
        +-----------+-----------+-----------+
        v           v           v           v
  :core:money   :core:ledger  :core:budget  :core:recurring     (pure Kotlin/JVM)
        ^           ^  ^           |             |
        +-----------+  +-----------+-------------+
          money <- ledger <- budget ; ledger <- recurring
```

`:app`, `:core:money` and `:core:ledger` exist today. `budget` and `recurring` are added in
the slice that needs them (plan §3.1). There is no `core/shared` package yet; promote shared code
to it the day a module that must not depend on `ledger` needs it (likely parsing in M1b).

## Call flow

UI (Compose) → ViewModel → core service → repository (Room dao) → db. Each layer calls only the
one below: no UI reaching into a dao, no data access in a core service, no business logic in a dao
or a composable.

## The core/app boundary

`core` defines the persistence contracts; `app` implements them with Room (built in A4a; tables and
conventions in [data-model.md](data-model.md)). These are the
only swap-boundary interfaces in M1a (CLAUDE.md abstraction bar). `:core:ledger` defines:

| Interface | Role | Room implementation (`dev.imagh.pft.app.data`) |
|---|---|---|
| `TransactionRunner` | `inTransaction { }`: the write and its audit entry commit or roll back together (INT-4) | `RoomTransactionRunner` over `withTransaction` |
| `AuditLog` | insert-only audit trail (INT-2); immutability also enforced by DB triggers | `RoomAuditLog` |
| `EntityRepository<T, Id>` | `get`, `getLive`, `insert`, `update`; extended by every entity's repository | `RoomEntityRepository` base |
| `IdentityRepository` | users and devices | `RoomIdentityRepository` |
| `AppSettingsRepository` | runtime settings | `RoomAppSettingsRepository` |

Core tests use in-memory fakes of all of these (`:core:ledger` test fixtures); the in-memory runner
snapshots and restores its state when a block throws, so atomicity is testable on a plain JVM.

## The audited write path

Every ledger entity (account, transaction, ...) is written through `AuditedWriter`, never by calling
a repository and the audit log separately:

```
service rule check -> AuditedWriter.create/update/delete/restore/archive
                        -> runner.inTransaction {
                             identity.requireIdentity()          // attribution (fails closed)
                             repository.insert/update(entity)    // meta stamped by the writer
                             recorder.record(AuditDiff.diff(old, new))   // same transaction
                           }
```

A no-op edit (no audited field changed) writes nothing. Deletes are tombstones (INT-3). Each
entity composes `EntityMeta` and implements `LedgerEntity`/`Auditable`; its service holds only its
own rules (the reuse-by-layer table is in `docs/architecture/code-structure.md`). First-run setup
(`IdentityService`) and runtime settings (`SettingsService`) are audited through the same
`AuditRecorder`, since they carry no `EntityMeta`.

## Build and quality gates

| Concern | Choice |
|---|---|
| Build | Gradle 8.10.2 wrapper (checksum-pinned), AGP 8.7.3, Kotlin 2.1.0, JDK 17 via `gradle/gradle-daemon-jvm.properties` |
| Shared build logic | included build `build-logic/` with the `pft.kotlin-core` and `pft.lint` plugins (see `build-logic/README.md`) |
| Pinned versions | `gradle/libs.versions.toml` (single catalog, also read by build-logic) |
| Supply chain | `gradle.lockfile` per project (root, `app`, each core module, `build-logic`) plus `buildscript-gradle.lockfile` (dependency locking), `gradle/verification-metadata.xml` (sha256), OWASP dependency-check on demand (`dependencyCheckAggregate`, not in `check`) |
| On commit | `./gradlew --quiet ktlintFormat detekt` (pre-commit); covers module sources, the root/settings/module build scripts and `build-logic` |
| On push | `./gradlew check`: unit and Robolectric tests, Android lint, ktlint, detekt, and the **JaCoCo 90% line-coverage floor on every core module** |

## UI layer

All UI is Compose in `:app` and is wrapped in `PftTheme` (`ui/theme`): light/dark resolution,
opt-in dynamic colour or the default Graphite scheme, and fixed money colours, provided through Compose CompositionLocals (no
new interfaces). Shared components live in `ui/common`. The design system (tokens, one-hand
layout, motion, icons) is specified in `docs/plans/2026-10-29-m1a-design-system.md`; what is built
is documented in `docs/architecture/design-system.md`. Slice DS1 delivered the colour tokens,
`PftTheme`, the day/night window themes and edge-to-edge; DS2 the type, shape and spacing tokens, the
bundled Newsreader serif, the Roborazzi screenshot harness and the debug design catalogue.

## Security posture of the shell

The manifest requests no permissions, disables backup, exports only the launcher activity and sets
`FLAG_SECURE`; `ManifestHardeningTest` pins this (details in `app/README.md`).
