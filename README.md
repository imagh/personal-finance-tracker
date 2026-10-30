# Personal Finance Tracker

A private, offline-first Android app for tracking a household's expenses, budgets, loans (EMIs) and goals. It logs transactions automatically from SMS and email, parsed on-device, with local encrypted SQLite storage. Personal use for two people; accuracy, integrity, privacy and security come first.

**Status:** Gradle skeleton and quality gates in place (M1a slice A1), `core/money`, the `core/ledger` foundations (A3) and the plaintext Room database v1 with its core adapters (A4a) built; no user-visible features yet. Current milestone: M1a (core ledger).

| Read | For |
|---|---|
| [docs/PRD.md](docs/PRD.md) | What we are building, decisions, open questions |
| [docs/TECHNICAL.md](docs/TECHNICAL.md) | Engineering rules: graphify, no secrets in the repo, starter kit, security review |
| [CLAUDE.md](CLAUDE.md) | Development constitution for agents and humans |
| [docs/guides/local-setup.md](docs/guides/local-setup.md) | Set up your machine |
| [docs/guides/collecting-sample-messages.md](docs/guides/collecting-sample-messages.md) | Export and redact sample SMS/email |
| [docs/research/indian-bank-sms.md](docs/research/indian-bank-sms.md) | Bank SMS format research |

**Module map:**

| Path | What |
|---|---|
| `app/` | Thin Android layer (Compose, Room database and core adapters; later SQLCipher, Keystore). See [app/README.md](app/README.md) |
| `core/money/` | Pure Kotlin integer-paise money: `Paise`, strict `PaiseParser`, Indian-grouping `PaiseFormat`. See [core/money/README.md](core/money/README.md) |
| `core/ledger/` | Pure Kotlin ledger foundations: UUIDv7 ids, identity, the audit trail and `AuditedWriter`, `TransactionRunner`, validation, runtime settings, plus shared test fixtures. See [core/ledger/README.md](core/ledger/README.md) |
| `core/<module>/` | Further pure-Kotlin modules (budget, recurring) are added by their slices |
| `build-logic/` | Convention plugins `pft.kotlin-core` and `pft.lint`. See [build-logic/README.md](build-logic/README.md) |
| `tools/redact/` | Local redaction tool (Python) |
| `tools/material-symbols/` | Importer for the Material Symbols drawables (Python; see `docs/architecture/design-system.md`, "Adding an icon") |
| `fixtures/` | Reviewed, redacted samples only (planned) |

**Build and test** (JDK 17 and the Android SDK from [local setup](docs/guides/local-setup.md); `ANDROID_HOME` must be set):

```bash
./gradlew ktlintFormat detekt     # format + lint (pre-commit hook)
./gradlew check                   # tests, Android lint, ktlint, detekt, 90% coverage floor on core (pre-push hook)
./gradlew :app:assembleDebug      # debug APK
./gradlew :app:assembleRelease    # minified (R8), unsigned release APK; `check` also runs :app:verifyReleaseShrink
./gradlew :app:recordRoborazziDebug  # re-record screenshot goldens after a deliberate visual change (`check` verifies them)
./gradlew dependencyCheckAggregate  # vulnerability gate on shipped code; needs NVD_API_KEY, OSSINDEX_USERNAME, OSSINDEX_TOKEN (PR security step)
./gradlew dependencyCheckAggregate -PscanAll  # report-only scan of all build/test tooling (PR security step)
```

Architecture: [docs/architecture/overview.md](docs/architecture/overview.md), [docs/architecture/code-structure.md](docs/architecture/code-structure.md), [docs/architecture/data-model.md](docs/architecture/data-model.md).

**Privacy:** raw SMS/email exports and unreviewed redaction output must never be committed. `.gitignore` blocks them.
