package io.hamal.lib.common.value

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

class DefaultEnvMetaTableTest {
    @Test
    fun `Every operation is covered`() {
        assertThat(DefaultEnvMetaTable.operators, hasSize(0))
    }

    @Test
    fun `Test type`() {
        assertThat(DefaultEnvMetaTable.type, equalTo("env"))
    }
}