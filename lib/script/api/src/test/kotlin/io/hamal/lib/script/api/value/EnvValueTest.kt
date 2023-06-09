package io.hamal.lib.script.api.value

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

class DefaultEnvValueMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultEnvValueMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultEnvValueMetaTable.type, equalTo("env"))
    }
}