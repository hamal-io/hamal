package io.hamal.lib.kua.value

import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.ResourceLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class AnyValueTest {

    @Test
    fun `AnyValue can be used with BooleanValue`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(
            Extension(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("pass_through", AnyValuePassThrough()),
                    NamedFunctionValue("captor", captor)
                )
            )
        )

        sandbox.runCode(
            """
            test.captor(test.pass_through(true))    
        """.trimIndent()
        )

        assertThat(
            captor.result, equalTo(
                AnyValue(
                    TrueValue
                )
            )
        )
    }

    @Test
    fun `AnyValue can be used with NumberValue`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(
            Extension(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("pass_through", AnyValuePassThrough()),
                    NamedFunctionValue("captor", captor)
                )
            )
        )

        sandbox.runCode(
            """
            test.captor(test.pass_through(23))    
        """.trimIndent()
        )

        assertThat(
            captor.result, equalTo(
                AnyValue(
                    NumberValue(23)
                )
            )
        )
    }

    @Test
    fun `AnyValue can be used with StringValue`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(
            Extension(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("pass_through", AnyValuePassThrough()),
                    NamedFunctionValue("captor", captor)
                )
            )
        )

        sandbox.runCode(
            """
            test.captor(test.pass_through("hamal.io"))    
        """.trimIndent()
        )

        assertThat(
            captor.result, equalTo(
                AnyValue(
                    StringValue("hamal.io")
                )
            )
        )
    }

    @Test
    @Disabled
    fun `AnyValue can be used with TableMapValue`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(
            Extension(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("pass_through", AnyValuePassThrough()),
                    NamedFunctionValue("captor", captor)
                )
            )
        )

        sandbox.runCode(
            """
            test.captor(test.pass_through({key="value"}))    
        """.trimIndent()
        )

        assertThat(
            captor.result, equalTo(
                AnyValue(
                    StringValue("hamal.io")
                )
            )
        )
    }

    private class AnyValuePassThrough : Function1In1Out<AnyValue, AnyValue>(
        FunctionInput1Schema(AnyValue::class),
        FunctionOutput1Schema(AnyValue::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: AnyValue): AnyValue {
            return arg1
        }
    }

    private class AnyValueResultCaptor : Function1In0Out<AnyValue>(
        FunctionInput1Schema(AnyValue::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: AnyValue) {
            result = arg1
        }

        var result: Value = NilValue
    }

    private val sandbox = run {
        ResourceLoader.load()
        Sandbox()
    }
}