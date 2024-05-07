package io.hamal.lib.common.util

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class SerdeStringUtilsTest {

    @TestFactory
    fun snakeCase() = listOf(
        "" to "",
        "snake_case" to "snake_case",
        "SNAKE" to "snake",
        "snakeCase" to "snake_case",
        "PascalCase" to "pascal_case"
    ).map { (given, expected) ->
        dynamicTest("snake_case: '$given' --> '$expected'") {
            assertThat(StringUtils.snakeCase(given), equalTo(expected))
        }
    }

}