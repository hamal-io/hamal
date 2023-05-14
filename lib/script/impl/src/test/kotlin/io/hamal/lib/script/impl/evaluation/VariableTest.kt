package io.hamal.lib.script.impl.evaluation

import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.TableEntry
import io.hamal.lib.script.api.value.TableValue
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