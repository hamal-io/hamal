package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class CodeValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            CodeValue("log.info('hamal rocks')"),
            """{"type":"CodeValue","value":"log.info('hamal rocks')"}"""
        ),
    ).flatten()
}

class DefaultCodeValueMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultCodeValueMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultCodeValueMetaTable.type, equalTo("code"))
    }
}