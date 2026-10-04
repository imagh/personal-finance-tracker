# Personal Finance Tracker — Learnings

A running ledger of decisions, gotchas, and non-obvious facts worth carrying into future
sessions (CLAUDE.md Rule B). Append at the end of every session/task; newest at the top. Keep
each entry short: what we learned, why it matters, and how to apply it next time.

---

<!-- Template for an entry:
### YYYY-MM-DD — <short title>
- **Learning:** <the non-obvious fact or decision>.
- **Why:** <why it matters / what it prevents>.
- **How to apply:** <what to do next time>.
-->

### 2026-10-04 — EMIs are expenses; transfers and card bill payments are not
- **Learning:** an EMI payment is an expense linked to its loan entity. Transfers between own accounts and credit-card bill payments are not income/expense.
- **Why:** counts spending once and keeps the loan's outstanding derivable from linked EMIs. A card purchase converted to EMI would double count, so the original purchase is neutralised by linking it to the loan (PRD LN-5).

### 2026-10-04 — Bank SMS are untrusted input; parsing is templates only
- **Learning:** sender headers can be spoofed and scam messages mimic bank alerts. Parsing is deterministic and on-device; auto-logged transactions are flagged unverified.
- **Why:** a forged alert must never be treated as fact, and no message text may leave the device.

### 2026-10-04 — Builds run locally; keep logic in pure-Kotlin `core`
- **Learning:** the owner builds locally on a Claude Pro plan. The cloud environment also blocked `dl.google.com` (Android SDK), which no longer matters.
- **Why:** `core` modules test on a plain JVM in seconds, keeping sessions short and cheap.

