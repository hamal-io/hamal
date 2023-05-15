package io.hamal.lib.script.impl.evaluation

import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.NumberValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class VariableTest : AbstractEvalTest() {
    @Test
    fun `Assign number to global variable`() {
        val result = eval("""some_number=2810""")
        assertThat(result, equalTo(NilValue))
        assertThat(testEnvironment["some_number"], equalTo(NumberValue(2810)))
    }

    @Test
    fun `Assign multiple values to multiple global variables at once`() {
        val result = eval("""x,y,z = 1,2,3""")

        assertThat(result, equalTo(NilValue))
        assertThat(testEnvironment["x"], equalTo(NumberValue(1)))
        assertThat(testEnvironment["y"], equalTo(NumberValue(2)))
        assertThat(testEnvironment["z"], equalTo(NumberValue(3)))
    }

    @Test
    fun `Assign nil to local variable`() {
        val result = eval("""local x = nil""")
        assertThat(result, equalTo(NilValue))
        assertThat(testEnvironment["x"], equalTo(NilValue))
    }

    @Test
    fun `Assign number to local variable`() {
        val result = eval("""local some_local_number = 1212""")

        assertThat(result, equalTo(NilValue))
        assertThat(testEnvironment["some_local_number"], equalTo(NumberValue(1212)))
    }

    @Test
    fun `Assign multiple values to multiple local variables at once`() {
        val result = eval("""local x,y,z = 1,2,3""")

        assertThat(result, equalTo(NilValue))
        assertThat(testEnvironment["x"], equalTo(NumberValue(1)))
        assertThat(testEnvironment["y"], equalTo(NumberValue(2)))
        assertThat(testEnvironment["z"], equalTo(NumberValue(3)))
    }
}