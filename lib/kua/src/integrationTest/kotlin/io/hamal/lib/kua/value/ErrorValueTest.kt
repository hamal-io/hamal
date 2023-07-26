package io.hamal.lib.kua.value

import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableMapProxyValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ErrorValueTest {

    @Test
    fun `Function value returns error value`() {
        val messageCaptor = Captor()

        sandbox.register(
            Extension(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("error", FunctionReturnsError()),
                    NamedFunctionValue("message_captor", messageCaptor),
                    NamedFunctionValue("assert_metatable", AssertMetatable)
                )
            )
        )

        sandbox.runCode(
            """
            local err = test.error()
            print(err.value)
            test.message_captor(err.value)
            
            --local x = {
            --    __type = 20,
            --    __typename = "error"
            --}
            --setmetatable(err,x)
            
            local mtbl = getmetatable(err)
            test.assert_metatable(mtbl)
        """.trimIndent()
        )

        assertThat(messageCaptor.result, equalTo(AnyValue(StringValue("Sometimes an error can be a good thing"))))
    }

    private object AssertMetatable : Function1In0Out<TableMapProxyValue>(
        FunctionInput1Schema(TableMapProxyValue::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: TableMapProxyValue) {
            assertThat(arg1.getInt("__type"), equalTo(20))
            assertThat(arg1.getString("__typename"), equalTo("error"))
        }
    }

    private class FunctionReturnsError : Function0In1Out<ErrorValue>(
        FunctionOutput1Schema(ErrorValue::class)
    ) {
        override fun invoke(ctx: FunctionContext): ErrorValue {
            return ErrorValue("Sometimes an error can be a good thing")
        }
    }

    private class Captor : Function1In0Out<AnyValue>(
        FunctionInput1Schema(AnyValue::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: AnyValue) {
            result = arg1
        }

        var result: AnyValue? = null
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox()
    }
}