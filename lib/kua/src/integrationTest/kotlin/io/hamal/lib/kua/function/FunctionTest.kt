package io.hamal.lib.kua.function

import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.ResourceLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


internal class FunctionTest {

    @Test
    fun `Tests Function0In1Out and Function1In0Out`() {
        val captor = Captor1()
        val emitter = object : Function0In1Out<StringValue>(FunctionOutput1Schema(StringValue::class)) {
            override fun invoke(ctx: FunctionContext): StringValue {
                return StringValue("Hamal Rocks")
            }
        }
        sandbox.register(
            Extension(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("emit", emitter),
                    NamedFunctionValue("capture", captor)
                )
            )
        )

        sandbox.runCode("test.capture(test.emit())")
        assertThat(captor.result, equalTo("Hamal Rocks"))
    }

    @Test
    fun `Tests Function1In1Out`() {
        val captor = Captor1()
        val transform = object : Function1In1Out<StringValue, StringValue>(
            FunctionInput1Schema(StringValue::class),
            FunctionOutput1Schema(StringValue::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: StringValue): StringValue {
                return StringValue(arg1.value.uppercase())
            }
        }

        sandbox.register(
            Extension(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("transform", transform),
                    NamedFunctionValue("capture", captor)
                )
            )
        )

        sandbox.runCode("test.capture(test.transform('some message'))")
        assertThat(captor.result, equalTo("SOME MESSAGE"))
    }

    @Test
    fun `Tests Function2In2Out`() {
        val captor = Captor2()
        val transform = object : Function2In2Out<StringValue, NumberValue, StringValue, NumberValue>(
            FunctionInput2Schema(StringValue::class, NumberValue::class),
            FunctionOutput2Schema(StringValue::class, NumberValue::class)
        ) {
            override fun invoke(
                ctx: FunctionContext,
                arg1: StringValue,
                arg2: NumberValue
            ): Pair<StringValue, NumberValue> {
                return StringValue(arg1.value.reversed()) to (arg2 * -1)
            }
        }

        sandbox.register(
            Extension(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("transform", transform),
                    NamedFunctionValue("capture", captor)
                )
            )
        )

        sandbox.runCode("test.capture(test.transform('lazy', 42))")
        assertThat(captor.result, equalTo("yzal=-42.0"))
    }

    @Test
    fun `Tests Function0In2Out and Function2In0Out`() {
        val captor = Captor2()
        val emitter = object :
            Function0In2Out<StringValue, NumberValue>(FunctionOutput2Schema(StringValue::class, NumberValue::class)) {
            override fun invoke(ctx: FunctionContext): Pair<StringValue, NumberValue> {
                return StringValue("answer") to NumberValue(42)
            }
        }

        sandbox.register(
            Extension(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("emit", emitter),
                    NamedFunctionValue("capture", captor)
                )
            )
        )

        sandbox.runCode("test.capture(test.emit())")
        assertThat(captor.result, equalTo("answer=42.0"))
    }

    private class Captor1 : Function1In0Out<StringValue>(FunctionInput1Schema(StringValue::class)) {
        override fun invoke(ctx: FunctionContext, arg1: StringValue) {
            result = arg1.value
        }

        var result: String? = null
    }

    private class Captor2 : Function2In0Out<StringValue, NumberValue>(
        FunctionInput2Schema(StringValue::class, NumberValue::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: StringValue, arg2: NumberValue) {
            result = "${arg1.value}=${arg2.value}"
        }

        var result: String? = null
    }

    private val sandbox = run {
        ResourceLoader.load()
        Sandbox()
    }
}
