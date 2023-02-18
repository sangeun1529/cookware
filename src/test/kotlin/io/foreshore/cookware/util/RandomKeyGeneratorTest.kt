package io.foreshore.cookware.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException

@DisplayName("RandomKeyGenerator Test")
class RandomKeyGeneratorTest : StringSpec({
    "Generator Simple" {
        val gen = RandomKeyGenerator()
        val actual = gen.nextKey(8)
        println(actual)
        actual.length shouldBe 8
    }

    "Illegal State Exception" {
        val gen = RandomKeyGenerator(predicate = { false })
        shouldThrow<IllegalStateException> {
            gen.nextKey()
        }
    }

    "Generator Long" {
        val gen = RandomKeyGenerator()
        val actual = gen.nextKey(32)
        println(actual)
        actual.length shouldBe 32
    }

    "Generator custom component" {
        val gen = RandomKeyGenerator(components = "a")
        val actual = gen.nextKey(8)
        println(actual)
        actual.length shouldBe 8
        actual shouldBe "aaaaaaaa"
    }

    "Generator component empty" {
        shouldThrow<IllegalArgumentException> {
            RandomKeyGenerator(components = "")
        }
    }

    "Generator with predicate" {
        val actual = RandomKeyGenerator(components = "01").nextKey(1) { it == "0" }
        print(actual)
        actual shouldBe "0"
    }
})
