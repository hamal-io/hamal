package io.hamal.lib.kua.function

import io.hamal.lib.kua.DefaultSandboxContext
import io.hamal.lib.kua.ExtensionError
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class FunctionTest {

    @Test
    fun `Functions throws exception`() {
        val neverCalled = object : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                called = true
            }

            var called = false
        }
        val throwException = object : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                throw IllegalArgumentException("you can not win this argument")
            }
        }
        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "throw_exception" to throwException,
                    "never_called" to neverCalled,
                )
            )
        )

        val exception = assertThrows<ExtensionError> {
            sandbox.load(
                """
                test.throw_exception()
                test.never_called()
            """
            )
        }

        assertThat(exception.cause, instanceOf(IllegalArgumentException::class.java))
        assertThat(exception.cause?.message, equalTo("you can not win this argument"))
        assertThat(neverCalled.called, equalTo(false))
    }

    @Test
    fun `Functions throws error`() {
        val neverCalled = object : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                called = true
            }

            var called = false
        }
        val throwError = object : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                throw Error("some error")
            }
        }
        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "throw_error" to throwError,
                    "never_called" to neverCalled,
                )
            )
        )

        val error = assertThrows<Error> {
            sandbox.load(
                """
                test.throw_error()
                test.never_called()
            """
            )
        }
        assertThat(error.cause, instanceOf(Error::class.java))
        assertThat(error.cause?.message, equalTo("some error"))
        assertThat(neverCalled.called, equalTo(false))
    }

    @Test
    fun `Tests Function0In1Out and Function1In0Out`() {
        val captor = Captor1()
        val emitter = object : Function0In1Out<StringType>(FunctionOutput1Schema(StringType::class)) {
            override fun invoke(ctx: FunctionContext): StringType {
                return StringType("Hamal Rocks")
            }
        }
        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "emit" to emitter,
                    "capture" to captor
                )
            )
        )

        sandbox.load("test.capture(test.emit())")
        assertThat(captor.result, equalTo("Hamal Rocks"))
    }

    @Test
    fun `Tests Function1In1Out`() {
        val captor = Captor1()
        val transform = object : Function1In1Out<StringType, StringType>(
            FunctionInput1Schema(StringType::class),
            FunctionOutput1Schema(StringType::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: StringType): StringType {
                return StringType(arg1.value.uppercase())
            }
        }

        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "transform" to transform,
                    "capture" to captor
                )
            )
        )

        sandbox.load("test.capture(test.transform('some message'))")
        assertThat(captor.result, equalTo("SOME MESSAGE"))
    }

    @Test
    fun `Tests Function1In2Out`() {
        val captor = Captor2()
        val transform = object : Function1In2Out<StringType, StringType, NumberType>(
            FunctionInput1Schema(StringType::class),
            FunctionOutput2Schema(StringType::class, NumberType::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<StringType, NumberType> {
                return StringType(arg1.value.uppercase()) to NumberType(arg1.value.length)
            }
        }

        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "transform" to transform,
                    "capture" to captor
                )
            )
        )

        sandbox.load("local x,y = test.transform('hamal'); test.capture(x,y)")
        assertThat(captor.result, equalTo("HAMAL=5.0"))
    }

    @Test
    fun `Tests Function2In2Out`() {
        val captor = Captor2()
        val transform = object : Function2In2Out<StringType, NumberType, StringType, NumberType>(
            FunctionInput2Schema(StringType::class, NumberType::class),
            FunctionOutput2Schema(StringType::class, NumberType::class)
        ) {
            override fun invoke(
                ctx: FunctionContext,
                arg1: StringType,
                arg2: NumberType
            ): Pair<StringType, NumberType> {
                return StringType(arg1.value.reversed()) to (arg2 * -1)
            }
        }

        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "transform" to transform,
                    "capture" to captor
                )
            )
        )

        sandbox.load("test.capture(test.transform('lazy', 42))")
        assertThat(captor.result, equalTo("yzal=-42.0"))
    }

    @Test
    fun `Tests Function0In2Out and Function2In0Out`() {
        val captor = Captor2()
        val emitter = object :
            Function0In2Out<StringType, NumberType>(FunctionOutput2Schema(StringType::class, NumberType::class)) {
            override fun invoke(ctx: FunctionContext): Pair<StringType, NumberType> {
                return StringType("answer") to NumberType(42)
            }
        }

        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "emit" to emitter,
                    "capture" to captor
                )
            )
        )

        sandbox.load("test.capture(test.emit())")
        assertThat(captor.result, equalTo("answer=42.0"))
    }

    private class Captor1 : Function1In0Out<StringType>(FunctionInput1Schema(StringType::class)) {
        override fun invoke(ctx: FunctionContext, arg1: StringType) {
            result = arg1.value
        }

        var result: String? = null
    }

    private class Captor2 : Function2In0Out<StringType, NumberType>(
        FunctionInput2Schema(StringType::class, NumberType::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: NumberType) {
            result = "${arg1.value}=${arg2.value}"
        }

        var result: String? = null
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(DefaultSandboxContext())
    }
}
