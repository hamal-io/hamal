package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class EvalAssignmentTest : AbstractEvalTest() {
    @Nested
    inner class LocalTest {
        @Test
        fun `Assigns a number to a local identifier`() {
            val result = eval("""local birthday = 2810""")
            assertThat(result, equalTo(NilValue))
            assertThat(testEnvironment["birthday"], equalTo(NumberValue(2810)))
        }

        @Test
        fun `Assigns a string to a local identifier`() {
            val result = eval("""local project_name = 'Hamal'""")
            assertThat(result, equalTo(NilValue))
            assertThat(testEnvironment["project_name"], equalTo(StringValue("Hamal")))
        }

        @Test
        fun `Assigns true to a local identifier`() {
            val result = eval("""local isFun = true""")
            assertThat(result, equalTo(NilValue))
            assertThat(testEnvironment["isFun"], equalTo(TrueValue))
        }

        @Test
        fun `Assigns false to a local identifier`() {
            val result = eval("""local isFailure = false""")
            assertThat(result, equalTo(NilValue))
            assertThat(testEnvironment["isFailure"], equalTo(FalseValue))
        }

        @Test
        fun `Assigns nil to a local identifier`() {
            val result = eval("""local value = nil""")
            assertThat(result, equalTo(NilValue))
            assertThat(testEnvironment["value"], equalTo(NilValue))
        }
    }

}