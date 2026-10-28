# Personal Finance Tracker

A private, offline-first Android app for tracking a household's expenses, budgets, loans (EMIs) and goals. It logs transactions automatically from SMS and email, parsed on-device, with local encrypted SQLite storage. Personal use for two people; accuracy, integrity, privacy and security come first.

**Status:** planning and setup. No application code yet. Current milestone: M1a (core ledger).

| Read | For |
|---|---|
| [docs/PRD.md](docs/PRD.md) | What we are building, decisions, open questions |
| [docs/TECHNICAL.md](docs/TECHNICAL.md) | Engineering rules: graphify, no secrets in the repo, starter kit, security review |
| [CLAUDE.md](CLAUDE.md) | Development constitution for agents and humans |
| [docs/guides/local-setup.md](docs/guides/local-setup.md) | Set up your machine |
| [docs/guides/collecting-sample-messages.md](docs/guides/collecting-sample-messages.md) | Export and redact sample SMS/email |
| [docs/research/indian-bank-sms.md](docs/research/indian-bank-sms.md) | Bank SMS format research |

**Layout (planned):** `app/` (thin Android layer), `core/<module>/` (pure Kotlin logic), `tools/redact/` (local redaction tool), `fixtures/` (reviewed, redacted samples only).

**Privacy:** raw SMS/email exports and unreviewed redaction output must never be committed. `.gitignore` blocks them.
