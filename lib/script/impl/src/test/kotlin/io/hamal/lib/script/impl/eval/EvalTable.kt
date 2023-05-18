package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class EvalTableTest : AbstractEvalTest() {
    @Test
    fun `Creates an empty table`() {
        val result = eval("""{ }""")
        assertThat(result, equalTo(TableValue()))
    }

    @Nested
    @DisplayName("Array")
    inner class ArrayTest {
        @Test
        fun `Creates an table with a single number`() {
            val result = eval("""{1212}""")
            assertThat(
                result, equalTo(
                    TableValue(
                        NumberValue(1) to NumberValue(1212)
                    )
                )
            )
        }

        @Test
        fun `Creates an table with different types`() {
            val result = eval("""{true, 1, 2, 'some-str', false, nil}""")
            assertThat(
                result, equalTo(
                    TableValue(
                        NumberValue(1) to TrueValue,
                        NumberValue(2) to NumberValue(1),
                        NumberValue(3) to NumberValue(2),
                        NumberValue(4) to StringValue("some-str"),
                        NumberValue(5) to FalseValue,
                        NumberValue(6) to NilValue
                    )
                )
            )
        }
    }

    @Nested
    @DisplayName("Map")
    inner class MapTest {
        @Test
        fun `Creates an table with a single number`() {
            val result = eval("""{some = 1212}""")
            assertThat(
                result, equalTo(
                    TableValue(
                        Identifier("some") to NumberValue(1212)
                    )
                )
            )
        }

        @Test
        fun `Creates and assigns table to local identifier`() {
            val result = eval("""local t = {some = 1212}""")
            assertThat(result, equalTo(NilValue))
            assertThat(
                testEnvironment["t"], equalTo(
                    TableValue(
                        Identifier("some") to NumberValue(1212)
                    )
                )
            )
        }

        @Test
        fun `Access local table by identifier`() {
            val result = eval(
                """
                local t = {some = 1212}
                t['some']
            """.trimIndent()
            )
            assertThat(result, equalTo(NumberValue(1212)))
        }
    }
}