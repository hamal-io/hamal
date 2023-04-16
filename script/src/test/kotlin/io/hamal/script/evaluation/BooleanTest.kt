package io.hamal.script.evaluation

import io.hamal.script.value.FalseValue
import io.hamal.script.value.TrueValue
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