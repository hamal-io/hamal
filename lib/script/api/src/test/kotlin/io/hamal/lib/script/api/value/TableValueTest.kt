package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
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
                23 to NumberValue(34)
            ),
            """{"type":"TableValue","entries":{"23":{"type":"NumberValue","value":"34"}}}"""
        ),
    ).flatten()
}

class DefaultTableValueMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultTableValueMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultTableValueMetaTable.type, equalTo("table"))
    }
}