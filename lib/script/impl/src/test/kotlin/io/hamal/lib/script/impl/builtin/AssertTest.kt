package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.StringValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("BuiltinAssert")
internal class AssertTest : AbstractBuiltinTest() {

    @Test
    fun `Always true`() {
        val result = eval("""assert(true)""".trimIndent())
        assertThat(result, equalTo(NilValue))
    }

    @Test
    fun `Evaluates expression to true`() {
        val result = eval("""assert(1 < 2)""".trimIndent())
        assertThat(result, equalTo(NilValue))
    }

    @Test
    fun `Always false`() {
        val result = eval("""assert(false)""".trimIndent())
        assertThat(result, equalTo(ErrorValue(StringValue("Assertion violated: 'false'"))))
    }

    @Test
    fun `Evaluates expression to false`() {
        val result = eval("""assert(2 < 1)""".trimIndent())
        assertThat(result, equalTo(ErrorValue(StringValue("Assertion violated: '2 < 1'"))))
    }

    @Test
    fun `Shows custom error message on assertion violation`() {
        val result = eval("""assert(false,'some deep and meaningful message')""".trimIndent())
        assertThat(result, equalTo(ErrorValue(StringValue("Assertion violated: 'some deep and meaningful message'"))))
    }


    @Test
    fun `Evaluate not to boolean`() {
        val result = eval("""assert(2810)""".trimIndent())
        assertThat(result, equalTo(ErrorValue(StringValue("Assertion of non boolean value is always false"))))
    }

}