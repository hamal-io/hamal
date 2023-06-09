package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class StringValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(StringValue("hamal"), """{"type":"StringValue","value":"hamal"}"""),
    ).flatten()
}

class DefaultStringValueMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultStringValueMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultStringValueMetaTable.type, equalTo("string"))
    }
}