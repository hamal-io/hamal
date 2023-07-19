package io.hamal.lib.kua.value

import io.hamal.lib.kua.value.ValueSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class TableValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(TableValue(), """{"type":"TableValue"}"""),
        generateTestCases(
            TableValue(
                "key" to StringValue("value")
            ),
            """{"type":"TableValue","entries":{"key":{"type":"StringValue","value":"value"}}}""".trimIndent()
        ),
        generateTestCases(
            TableValue(
                23 to DecimalValue(34)
            ),
            """{"type":"TableValue","entries":{"23":{"type":"DecimalValue","value":"34"}}}"""
        ),
    ).flatten()
}