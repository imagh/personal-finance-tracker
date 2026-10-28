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

### 2026-10-28 — graphify: commit the graph, not the machine
- **Learning:** `graphify claude install` writes the hook with an absolute path to one user's home and leaves a `settings.json.graphify-bak`. `graphify-out/` also holds a manifest with absolute paths, a cache and backups. `graphify update .` refreshes only code nodes, never docs.
- **Why:** a committed absolute path breaks the hook on the second machine, and committing the rest of `graphify-out/` leaks local paths and churns diffs.
- **How to apply:** hooks call `$HOME/.local/bin/graphify`; `.gitignore` lets through only `graph.json` and `GRAPH_REPORT.md`. Re-read the hook on every graphify upgrade. Use `/graphify . --update` when docs change.

### 2026-10-28 — Develop on the emulator; the primary phone never gets USB debugging
- **Learning:** the owner's phone runs payment apps, which often refuse to work with Developer options or USB debugging on. The emulator is the development device, and it can receive fake SMS (`adb emu sms send`), which suits M1b. On Apple Silicon, API 35 images fall back to software rendering unless started with `-gpu host`.
- **Why:** a sideloaded app can only be updated with the same signing key, and uninstalling wipes the encrypted database.
- **How to apply:** only release-signed builds go to the phone, installed from a file; create the release keystore before the first phone install. Feed the emulator only redacted, reviewed fixture text.

### 2026-10-28 — Toolchain pinned about two years old; the build tool is not the IDE
- **Learning:** the build toolchain (AGP 8.7.3, Gradle 8.10.2, Kotlin 2.1.0, JDK 17) is pinned to late 2024, but Android Studio stays current: a newer Studio opens an older AGP project fine. In current Studio the per-project JDK is the Gradle *JVM criteria* (`gradle/gradle-daemon-jvm.properties`); a JDK path in the Gradle *Distribution* field fails with "Gradle location is incorrect".
- **Why:** an old IDE misses security fixes and may not run on the current macOS; JVM criteria make Studio and `./gradlew` use the same JDK on both developers' machines.
- **How to apply:** bump the toolchain only deliberately and re-check the AGP/Kotlin/Gradle tables (TECHNICAL.md §7.1). Bend the age rule for security (JDK patches, SQLCipher, anything the vulnerability scan flags). AGP 8.7 caps compileSdk at 35.

### 2026-10-04 — EMIs are expenses; transfers and card bill payments are not
- **Learning:** an EMI payment is an expense linked to its loan entity. Transfers between own accounts and credit-card bill payments are not income/expense.
- **Why:** counts spending once and keeps the loan's outstanding derivable from linked EMIs. A card purchase converted to EMI would double count, so the original purchase is neutralised by linking it to the loan (PRD LN-5).

### 2026-10-04 — Bank SMS are untrusted input; parsing is templates only
- **Learning:** sender headers can be spoofed and scam messages mimic bank alerts. Parsing is deterministic and on-device; auto-logged transactions are flagged unverified.
- **Why:** a forged alert must never be treated as fact, and no message text may leave the device.

### 2026-10-04 — Builds run locally; keep logic in pure-Kotlin `core`
- **Learning:** the owner builds locally on a Claude Pro plan. The cloud environment also blocked `dl.google.com` (Android SDK), which no longer matters.
- **Why:** `core` modules test on a plain JVM in seconds, keeping sessions short and cheap.
