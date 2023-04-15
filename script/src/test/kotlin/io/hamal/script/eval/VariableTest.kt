package io.hamal.script.eval

import io.hamal.script.value.NumberValue
import io.hamal.script.value.StringValue
import io.hamal.script.value.TableValue
import io.hamal.script.value.TableValue.TableEntry
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class VariableTest : AbstractEvalTest(){
    @Test
    fun `Assign number to global variable`(){
        val result = eval(
            """
            some_number=2810              
            """.trimIndent()
        )
        assertThat(result, equalTo(
            TableValue(
                TableEntry(
                    StringValue("some_number"),
                    NumberValue(2810)
                )
            )
        ))
    }

    @Test
    fun `Assign number to local variable`(){
        val result = eval(
            """
                local some_local_number = 1212
            """.trimIndent()
        )

        assertThat(result, equalTo(
            TableValue(
                TableEntry(
                    StringValue("some_local_number"),
                    NumberValue(1212)
                )
            )
        ))
    }
}