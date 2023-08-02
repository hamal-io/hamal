package io.hamal.lib.kua.value

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableMapValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ErrorValueTest {

    @Test
    fun `Function value returns error value`() {
        val messageCaptor = Captor()

        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "error" to FunctionReturnsError(),
                    "message_captor" to messageCaptor,
                    "assert_metatable" to AssertMetatable
                )
            )
        )

        sandbox.load(
            """
            local err = test.error()
            test.message_captor(err.message)
               
            local mtbl = getmetatable(err)
            test.assert_metatable(mtbl)
        """.trimIndent()
        )

        assertThat(messageCaptor.result, equalTo(AnyValue(StringValue("Sometimes an error can be a good thing"))))
    }

    @Test
    @Disabled
    fun `Tries to invoke function without argument`() {
        val errorCaptor = Captor()

        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "call" to FunctionNeverInvoked(),
                    "captor" to errorCaptor,
                    "assert_metatable" to AssertMetatable
                )
            )
        )

        sandbox.load(
            """
            local err = test.call()
            test.captor(err)
        """.trimIndent()
        )

        assertThat(errorCaptor.result, equalTo(AnyValue(ErrorValue("Sometimes an error can be a good thing"))))
    }


    private object AssertMetatable : Function1In0Out<TableMapValue>(
        FunctionInput1Schema(TableMapValue::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: TableMapValue) {
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


    private class FunctionNeverInvoked : Function1In0Out<NumberValue>(
        FunctionInput1Schema(NumberValue::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: NumberValue) {
            TODO("Not yet implemented")
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