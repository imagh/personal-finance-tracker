# `:core:money` — money (stub)

## Purpose

Integer-paise money: the `Paise` value type, strict parsing from user text, Indian digit-grouping
formatting and overflow-safe arithmetic (plan §3.2 decision 1). Pure Kotlin/JVM, no Android imports.

Status: **empty module.** A1 only creates it so the `pft.kotlin-core` convention plugin and its
gates (JUnit 5 + Kotest, JaCoCo 90% floor, ktlint, detekt) are wired and proven. The real README
(public interface, data, configuration) and the code arrive in slice A2.

## How to run and test

```bash
./gradlew :core:money:check
```
