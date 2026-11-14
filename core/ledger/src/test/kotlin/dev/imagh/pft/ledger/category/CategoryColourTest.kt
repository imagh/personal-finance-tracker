package dev.imagh.pft.ledger.category

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CategoryColourTest :
    FunSpec({
        test("the 12 permanent keys, in palette order") {
            // Persisted in category.colour: renaming or reordering any of these breaks stored data.
            CategoryColour.entries.map { it.key } shouldBe
                listOf(
                    "rose",
                    "coral",
                    "amber",
                    "olive",
                    "emerald",
                    "teal",
                    "sky",
                    "blue",
                    "indigo",
                    "violet",
                    "magenta",
                    "slate",
                )
        }

        test("every stored key reads back as its colour") {
            CategoryColour.entries.forEach { CategoryColour.fromStored(it.key) shouldBe it }
            CategoryColour.fromStored("teal") shouldBe CategoryColour.TEAL
        }

        test("an unknown, wrong-case, padded, empty or null value is slate") {
            listOf("TEAL", "Teal", " teal", "teal ", "", "chartreuse", "slate2").forEach {
                CategoryColour.fromStored(it) shouldBe CategoryColour.SLATE
            }
            CategoryColour.fromStored(null) shouldBe CategoryColour.SLATE
        }
    })
