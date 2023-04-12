package io.hamal.module.worker.script.eval

import io.hamal.module.worker.script.value.FalseValue
import io.hamal.module.worker.script.value.TrueValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class BooleanTest : AbstractEvalTest() {
    @Test
    fun `true`() {
        val result = eval("true")
        assertThat(result, equalTo(TrueValue))
    }

    @Test
    fun `false`() {
        val result = eval("false")
        assertThat(result, equalTo(FalseValue))
    }
}