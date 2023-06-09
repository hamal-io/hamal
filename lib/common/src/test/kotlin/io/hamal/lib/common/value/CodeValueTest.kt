package io.hamal.lib.common.value

import io.hamal.lib.common.value.ValueSerializationFixture.generateTestCases
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

class DefaultCodeMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultCodeMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultCodeMetaTable.type, equalTo("code"))
    }
}