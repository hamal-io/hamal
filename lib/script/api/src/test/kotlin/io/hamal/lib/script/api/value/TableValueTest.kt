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
        generateTestCases(TableValue.empty(), """{"type":"TableValue","entries":[]}"""),
        generateTestCases(
            TableValue(
                listOf(TableEntry(StringValue("key"), StringValue("io/hamal/lib/script/api/value")))
            ),
            """{"type":"TableValue","entries":[{"key":{"type":"StringValue","value":"key"},"value":{"type":"StringValue","value":"value"}}]}"""
        ),
        generateTestCases(
            TableValue(
                listOf(TableEntry(NumberValue(23), NumberValue(34)))
            ),
            """{"type":"TableValue","entries":[{"key":{"type":"NumberValue","value":"23"},"value":{"type":"NumberValue","value":"34"}}]}"""
        ),
    ).flatten()
}

class DefaultTableMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultTableMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultTableMetaTable.type, equalTo("table"))
    }
}