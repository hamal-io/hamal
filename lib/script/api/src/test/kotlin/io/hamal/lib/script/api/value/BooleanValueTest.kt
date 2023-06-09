package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class TrueValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(TrueValue, """{"type":"TrueValue"}"""),
    ).flatten()
}

class FalseValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(FalseValue, """{"type":"FalseValue"}"""),
    ).flatten()
}

class DefaultBooleanMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultBooleanMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultBooleanMetaTable.type, equalTo("boolean"))
    }
}