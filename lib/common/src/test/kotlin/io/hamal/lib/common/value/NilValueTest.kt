package io.hamal.lib.common.value

import io.hamal.lib.common.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class NilValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(NilValue, """{"type":"NilValue"}"""),
    ).flatten()
}

class DefaultNilMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultNilMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultNilMetaTable.type, equalTo("nil"))
    }
}