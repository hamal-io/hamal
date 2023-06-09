package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueSerializationFixture.generateTestCases
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class IdentValueTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(IdentValue("hamal"), """{"type":"IdentValue","value":"hamal"}"""),
    ).flatten()
}

class DefaultIdentMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultIdentMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultIdentMetaTable.type, equalTo("ident"))
    }
}