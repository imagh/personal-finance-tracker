package dev.imagh.pft.ledger.matchers

import dev.imagh.pft.ledger.entity.LedgerEntity
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Test-time enforcement that an entity's [LedgerEntity.auditFields] is **complete**, so no
 * persisted field can silently escape the audit trail (INT-2, CLAUDE.md §3.8).
 *
 * `AuditedWriter` can only see fields that `auditFields()` lists, so a forgotten field would be
 * written without an audit entry whenever it changes together with a listed one. This check closes
 * that gap with plain Java reflection (`declaredFields`; no kotlin-reflect, nothing in main
 * sources). It runs automatically on every insert and update of `InMemoryEntityRepository`, so any
 * service test that writes an entity through a fake repository exercises it, and is available as
 * [shouldHaveCompleteAuditFields] for direct use in an entity's own test.
 *
 * **The rules (strict, and all enforced):**
 * 1. Every field declared on the entity class has a key in `auditFields()` named by
 *    [columnName] (camelCase -> snake_case: `openingBalance` -> `opening_balance`).
 * 2. Every `auditFields()` key matches such a field. A misspelt or extra key fails, so the two
 *    cannot drift apart in either direction.
 * 3. For text, whole numbers, booleans, enums (by name), `Instant`, `LocalDate` and
 *    `LocalDateTime` (ISO text) the audited value must equal the field's value. Other types
 *    (for example a boxed value class such as `Paise?`) are checked by name only.
 *
 * **Not checked:** `meta` (the shared columns; the writer audits `archived_at` itself), and
 * `auditType`/`auditId` (interface members, not persisted). A field that is *not* persisted is not
 * allowed on an entity: compute such a value in a getter (`val x get() = ...`), which has no
 * backing field. Typed ids and `Paise` need no special case: a Kotlin value-class property keeps
 * its property name and is stored in the field as its underlying `String`/`Long`.
 */
object AuditFieldsCheck {
    private val EXEMPT_FIELDS = setOf("meta", "auditType", "auditId")
    private val ACRONYM_BOUNDARY = Regex("([A-Z]+)([A-Z][a-z])")
    private val WORD_BOUNDARY = Regex("([a-z0-9])([A-Z])")

    /** The audit key (and column name) for a Kotlin property: `bankRefID` -> `bank_ref_id`. */
    fun columnName(property: String): String =
        property
            .replace(ACRONYM_BOUNDARY, "$1_$2")
            .replace(WORD_BOUNDARY, "$1_$2")
            .lowercase()

    /**
     * Everything wrong with [entity]'s `auditFields()`, as messages that name fields and keys but
     * never print values. Empty when the entity is complete.
     */
    fun problems(entity: LedgerEntity<*, *>): List<String> {
        val fields = persistedFields(entity)
        val audited = entity.auditFields()
        val byKey = fields.associateBy { columnName(it.name) }
        val problems = mutableListOf<String>()
        for ((key, field) in byKey) {
            if (key !in audited) {
                problems += "field '${field.name}' is not audited: add \"$key\" to auditFields()"
            } else if (!valueMatches(field, entity, audited[key])) {
                problems +=
                    "field '${field.name}' is audited under \"$key\" with a different value than the field holds"
            }
        }
        for (key in audited.keys - byKey.keys) {
            problems += "auditFields() key \"$key\" matches no field " +
                "(expected one of ${byKey.keys.sorted()}; names are snake_case of the property)"
        }
        return problems
    }

    private fun persistedFields(entity: LedgerEntity<*, *>): List<Field> =
        entity.javaClass.declaredFields.filter {
            !Modifier.isStatic(it.modifiers) && !it.isSynthetic && it.name !in EXEMPT_FIELDS
        }

    private fun valueMatches(
        field: Field,
        entity: LedgerEntity<*, *>,
        audited: String?,
    ): Boolean {
        field.isAccessible = true
        val value = field.get(entity)
        return when {
            value == null -> audited == null
            value is Enum<*> -> audited == value.name
            value is String ||
                value is Number ||
                value is Boolean ||
                value is Char ||
                value is Instant ||
                value is LocalDate ||
                value is LocalDateTime -> audited == value.toString()
            else -> true
        }
    }
}

/** Passes when the entity's `auditFields()` is complete; see [AuditFieldsCheck]. */
fun haveCompleteAuditFields(): Matcher<LedgerEntity<*, *>> =
    Matcher { entity ->
        val problems = AuditFieldsCheck.problems(entity)
        MatcherResult(
            problems.isEmpty(),
            { "${entity.auditType} has an incomplete auditFields(): ${problems.joinToString("; ")}" },
            { "${entity.auditType} has a complete auditFields(), but it should not" },
        )
    }

/**
 * Asserts the entity's `auditFields()` lists every persisted field under its snake_case name and
 * nothing else (see [AuditFieldsCheck]). Call it once in each entity's own test; the fake
 * repository also calls it on every write.
 */
fun LedgerEntity<*, *>.shouldHaveCompleteAuditFields() = this should haveCompleteAuditFields()
