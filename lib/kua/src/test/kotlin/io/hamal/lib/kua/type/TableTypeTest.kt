package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class TableTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(TableType(), """{"type":"TableType"}"""),
        generateTestCases(
            TableType(
                "key" to StringType("value")
            ),
            """{"type":"TableType","entries":{"key":{"type":"StringType","value":"value"}}}""".trimIndent()
        ),
        generateTestCases(
            TableType(
                23 to DoubleType(34)
            ),
            """{"type":"TableType","entries":{"23":{"type":"DoubleType","value":"34"}}}"""
        ),
    ).flatten()
}