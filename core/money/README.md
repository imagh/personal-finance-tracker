# `:core:money` — integer-paise money

## Purpose

Money for the whole app, as integer paise and nothing else: the `Paise` value type with exact
arithmetic, strict parsing of user-typed amounts, and Indian-digit-grouping formatting. Floating
point is never used (CLAUDE.md §3.8, INT-1). Pure Kotlin/JVM, no Android imports, no third-party
runtime dependencies.

## Responsibilities & boundaries

- **Owns:** the representation of an amount (`Paise`), the grammar for amount text
  (`PaiseParser`), and the one display format (`PaiseFormat`).
- **Does not own:** business rules about amounts (positive, non-zero, per-entity limits belong to
  `:core:ledger` validation); sign glyphs, colours and tone (UI, `MoneyText`); currency (INR only);
  persistence (`app` maps `Paise.value` to an `INTEGER` column).
- **Untrusted input:** `PaiseParser` is the boundary for amount text. Everything it returns is in
  `0..Paise.MAX_AMOUNT`; everything else is rejected.

## Public interface

Package `dev.imagh.pft.money`.

| API | Behaviour |
|-----|-----------|
| `@JvmInline value class Paise(val value: Long)` | Signed (balances can be negative). `+` and `-` use `Math.addExact`/`subtractExact`: **overflow throws `ArithmeticException`**, it never wraps. `Comparable`. |
| `Paise.ZERO` | Zero paise. |
| `Paise.MAX_AMOUNT` | Rs 1e11 = `10_000_000_000_000` paise, the largest amount accepted from text (plan §3.2 decision 8). `:core:ledger` reuses it for its amount limit. |
| `PaiseParser.parse(text): Paise?` | Strict parse; **returns `null` on rejection** (never throws). |
| `PaiseParser.MAX_INPUT_LENGTH` | `32`; longer input is rejected before it is scanned. |
| `PaiseFormat.format(Paise): String` | `Paise(12345678)` -> `"₹1,23,456.78"`. Always two decimals. **Sign-free** (see below). |

### Parsing grammar

```
amount   = [ "₹" ] integer [ "." digit [ digit ] ]
integer  = digit+ | indian | western
indian   = d{1,2} ( "," d{2} )* "," d{3}        1,23,456
western  = d{1,3} ( "," d{3} )+                 123,456
```

- Digits are ASCII `0-9` only. No sign, exponent, whitespace (callers trim first), Unicode digits,
  trailing dot (`"12."`) or missing integer part (`".5"`).
- One decimal digit means tenths: `"0.5"` is 50 paise. Three or more decimals are rejected, never rounded.
- A leading `₹` is accepted so that `parse(format(x)) == x`.
- Rejected: `"1.234"`, `"-5"`, `"1e3"`, `""`, `" "`, `"12..3"`, `"1,2,3"`, `"१२३"`, anything above
  `MAX_AMOUNT` (even by one paisa) and anything longer than `MAX_INPUT_LENGTH`.
- The scan is a single linear pass with no regex, so hostile input cannot cause backtracking or
  overflow (the digit accumulator saturates, and the value is bounds-checked before use).
- Zero is accepted (`"0"`); whether zero is a valid transaction amount is the ledger's rule.

### Formatting and the sign (decision)

`PaiseFormat.format` **never emits a sign.** The caller prepends it: the design system's
`MoneyText` shows an expense as U+2212 MINUS SIGN + `format(...)`, e.g. `−₹1,23,456.78`, and
income with `+`. Consequently:

- A **negative `Paise` formats as its magnitude** (`format(Paise(-5)) == "₹0.05"`). A signed balance
  can therefore be passed straight in, with the caller choosing the glyph from `Paise.value`.
- `Long.MIN_VALUE` is safe: the digits are taken from the decimal string, not from `abs()`.

Grouping: the last three integer digits, then pairs (`999`, `1,000`, `99,999`, `1,00,000`,
`1,00,00,000`).

## Data it owns

None. It is stateless; `Paise` values are persisted by `app` as `INTEGER` paise.

## Configuration

None at runtime. `Paise.MAX_AMOUNT` and `PaiseParser.MAX_INPUT_LENGTH` are static, hardcoded limits
(CLAUDE.md §3.5: static values live in code/env, not the DB). They are not user-tunable.

## Dependencies

JDK only at runtime. Tests use JUnit 5 and Kotest (runner, assertions, property testing), supplied
by the `pft.kotlin-core` convention plugin. Depends on no other module; `:core:ledger` and `:app`
depend on it.

## How to run and test

```bash
./gradlew :core:money:check     # tests + ktlint + detekt + the 90% JaCoCo line-coverage gate
./gradlew :core:money:test      # tests only
```

Tests: `PaiseTest` (arithmetic, overflow, ordering, `a + b - b == a` property), `PaiseParserTest`
(accept/reject tables, limits, `parse(format(x)) == x` over `0..MAX_AMOUNT` and a never-throws
property), `PaiseFormatTest` (grouping boundaries, sign-free output, `Long.MIN_VALUE`).
