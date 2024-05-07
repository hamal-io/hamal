package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.CronPattern.Companion.CronPattern
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal object CronPatternTest {

    @TestFactory
    fun valid() = listOf(
        "* * * * * *",
        "0 0 12 * * ?",
        "0 30 21 ? * *",
        "0 0 2 ? * Mon-Fri",
        "0 10/30 1,22 L * ?"
    ).map { pattern ->
        dynamicTest(pattern) {
            CronPattern(pattern)
        }
    }

    @TestFactory
    fun invalid() = listOf(
        "",
        "        ",
        "\t",
        "* * * * * #",
        "0 */8 * * *",
        "0 10 ? * FRI *"
    ).map { pattern ->
        dynamicTest("invalid: $pattern") {
            assertThrows<IllegalArgumentException> {
                CronPattern(pattern)
            }.let { exception ->
                assertThat(exception.message, equalTo("Invalid Cron Expression"))
            }
        }
    }

}