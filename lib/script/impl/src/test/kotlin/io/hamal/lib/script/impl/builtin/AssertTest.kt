package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.StringValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


internal class AssertTest : AbstractBuiltinTest() {

    @Test
    fun `Always true`() {
        val result = eval("""assert(true)""")
        assertThat(result, equalTo(NilValue))
    }

    @Test
    fun `Evaluates expression to true`() {
        val result = eval("""assert(1 < 2)""")
        assertThat(result, equalTo(NilValue))
    }

    @Test
    fun `Always false`() {
        val result = expectError("""assert(false)""")
        assertThat(result, equalTo(ErrorValue(StringValue("Assertion violated: 'false'"))))
    }

    @Test
    fun `Evaluates expression to false`() {
        val result = expectError("""assert(2 < 1)""")
        assertThat(result, equalTo(ErrorValue(StringValue("Assertion violated: '2 < 1'"))))
    }

    @Test
    fun `Shows custom error message on assertion violation`() {
        val result = expectError("""assert(false,'some deep and meaningful message')""")
        assertThat(result, equalTo(ErrorValue(StringValue("Assertion violated: 'some deep and meaningful message'"))))
    }


    @Test
    fun `Evaluate not to boolean`() {
        val result = expectError("""assert(2810)""")
        assertThat(result, equalTo(ErrorValue(StringValue("Assertion of non boolean value is always false"))))
    }

}