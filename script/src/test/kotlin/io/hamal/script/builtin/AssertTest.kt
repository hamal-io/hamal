package io.hamal.script.builtin

import io.hamal.script.value.ErrorValue
import io.hamal.script.value.NilValue
import io.hamal.script.value.StringValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class AssertTest : AbstractBuiltinTest() {

    @Test
    fun `Assertion is always true`() {
        val result = eval(
            """
                assert(true)
            """.trimIndent()
        )
        assertThat(result, equalTo(NilValue))
    }

    @Test
    fun `Assertion is always false`() {
        val result = eval(
            """
                assert(false)
            """.trimIndent()
        )
        assertThat(result, equalTo(ErrorValue(StringValue("Assertion violated: 'false'"))))
    }

}