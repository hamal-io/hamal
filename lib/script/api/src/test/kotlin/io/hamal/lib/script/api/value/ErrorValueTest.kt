package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class ErrorValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            ErrorValue("Some error message"),
            """{"type":"ErrorValue","message":"Some error message"}"""
        ),
    ).flatten()
}

class DefaultErrorMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultErrorMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultErrorMetaTable.type, equalTo("error"))
    }
}