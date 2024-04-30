package io.hamal.lib.kua.function

import io.hamal.lib.kua.ExtensionError
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString
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
            RunnerPlugin(
                name = ValueString("test"),
                factoryCode = KuaCode(
                    """
                    function plugin_create(internal)
                        local export = { 
                            throw_exception =  internal.throw_exception,
                            never_called =  internal.never_called
                        }
                        return export
                    end
                """.trimIndent()
                ),
                internals = mapOf(
                    ValueString("throw_exception") to throwException,
                    ValueString("never_called") to neverCalled,
                )
            )
        )

        val exception = assertThrows<ExtensionError> {
            sandbox.codeLoad(
                KuaCode(
                    """
                test = require_plugin('test')
                test.throw_exception()
                test.never_called()
                """
                )
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
            RunnerPlugin(
                name = ValueString("test"),
                factoryCode = KuaCode(
                    """
                    function plugin_create(internal)
                        local export = { 
                            throw_error =  internal.throw_error,
                            throw_error =  internal.throw_error
                        }
                        return export
                    end
                """.trimIndent()
                ),
                internals = mapOf(
                    ValueString("throw_error") to throwError,
                    ValueString("never_called") to neverCalled,
                )
            )
        )

        val error = assertThrows<Error> {
            sandbox.codeLoad(
                KuaCode(
                    """
                test = require_plugin('test')
                test.throw_error()
                test.never_called()
                """
                )
            )
        }
        assertThat(error.cause, instanceOf(Error::class.java))
        assertThat(error.cause?.message, equalTo("some error"))
        assertThat(neverCalled.called, equalTo(false))
    }

    @Test
    fun `Tests Function0In1Out and Function1In0Out`() {
        val captor = Captor1()
        val emitter = object : Function0In1Out<ValueString>(FunctionOutput1Schema(ValueString::class)) {
            override fun invoke(ctx: FunctionContext): ValueString {
                return ValueString("Hamal Rocks")
            }
        }
        sandbox.register(
            RunnerPlugin(
                name = ValueString("test"),
                factoryCode = KuaCode(
                    """
                    function plugin_create(internal)
                        local export = { 
                            emit =  internal.emit,
                            capture =  internal.capture
                        }
                        return export
                    end
                """.trimIndent()
                ),
                internals = mapOf(
                    ValueString("emit") to emitter,
                    ValueString("capture") to captor
                )
            )
        )

        sandbox.codeLoad(
            KuaCode(
                """
            test = require_plugin('test')
            test.capture(test.emit())
        """.trimIndent()
            )
        )
        assertThat(captor.result, equalTo("Hamal Rocks"))
    }

    @Test
    fun `Tests Function1In1Out`() {
        val captor = Captor1()
        val transform = object : Function1In1Out<ValueString, ValueString>(
            FunctionInput1Schema(ValueString::class),
            FunctionOutput1Schema(ValueString::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: ValueString): ValueString {
                return ValueString(arg1.stringValue.uppercase())
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = ValueString("test"),
                factoryCode = KuaCode(
                    """
                    function plugin_create(internal)
                        local export = { 
                            transform =  internal.transform,
                            capture =  internal.capture
                        }
                        return export
                    end
                """.trimIndent()
                ),
                internals = mapOf(
                    ValueString("transform") to transform,
                    ValueString("capture") to captor
                )
            )
        )

        sandbox.codeLoad(
            KuaCode(
                """
            test = require_plugin('test')
            test.capture(test.transform('some message'))
        """
            )
        )
        assertThat(captor.result, equalTo("SOME MESSAGE"))
    }

    @Test
    fun `Tests Function1In2Out`() {
        val captor = Captor2()
        val transform = object : Function1In2Out<ValueString, ValueString, ValueNumber>(
            FunctionInput1Schema(ValueString::class),
            FunctionOutput2Schema(ValueString::class, ValueNumber::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<ValueString, ValueNumber> {
                return ValueString(arg1.stringValue.uppercase()) to ValueNumber(arg1.stringValue.length)
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = ValueString("test"),
                factoryCode = KuaCode(
                    """
                    function plugin_create(internal)
                        local export = { 
                            transform =  internal.transform,
                            capture =  internal.capture
                        }
                        return export
                    end
                """.trimIndent()
                ),
                internals = mapOf(
                    ValueString("transform") to transform,
                    ValueString("capture") to captor
                )
            )
        )

        sandbox.codeLoad(
            KuaCode(
                """
            test = require_plugin('test')
            local x,y = test.transform('hamal')
            test.capture(x,y)
        """
            )
        )
        assertThat(captor.result, equalTo("HAMAL=5.0"))
    }

    @Test
    fun `Tests Function2In2Out`() {
        val captor = Captor2()
        val transform = object : Function2In2Out<ValueString, ValueNumber, ValueString, ValueNumber>(
            FunctionInput2Schema(ValueString::class, ValueNumber::class),
            FunctionOutput2Schema(ValueString::class, ValueNumber::class)
        ) {
            override fun invoke(
                ctx: FunctionContext,
                arg1: ValueString,
                arg2: ValueNumber
            ): Pair<ValueString, ValueNumber> {
                return ValueString(arg1.stringValue.reversed()) to (arg2 * -1)
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = ValueString("test"),
                factoryCode = KuaCode(
                    """
                    function plugin_create(internal)
                        local export = { 
                            transform =  internal.transform,
                            capture =  internal.capture
                        }
                        return export
                    end
                """.trimIndent()
                ),
                internals = mapOf(
                    ValueString("transform") to transform,
                    ValueString("capture") to captor
                )
            )
        )

        sandbox.codeLoad(
            KuaCode(
                """
            test = require_plugin('test')
            test.capture(test.transform('lazy', 42))
        """
            )
        )
        assertThat(captor.result, equalTo("yzal=-42.0"))
    }

    @Test
    fun `Tests Function0In2Out and Function2In0Out`() {
        val captor = Captor2()
        val emitter = object :
            Function0In2Out<ValueString, ValueNumber>(FunctionOutput2Schema(ValueString::class, ValueNumber::class)) {
            override fun invoke(ctx: FunctionContext): Pair<ValueString, ValueNumber> {
                return ValueString("answer") to ValueNumber(42)
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = ValueString("test"),
                factoryCode = KuaCode(
                    """
                    function plugin_create(internal)
                        local export = { 
                            emit =  internal.emit,
                            capture =  internal.capture
                        }
                        return export
                    end
                """.trimIndent()
                ),
                internals = mapOf(
                    ValueString("emit") to emitter,
                    ValueString("capture") to captor
                )
            )
        )

        sandbox.codeLoad(
            KuaCode(
                """
            test = require_plugin('test')
            test.capture(test.emit())
        """
            )
        )
        assertThat(captor.result, equalTo("answer=42.0"))
    }

    private class Captor1 : Function1In0Out<ValueString>(FunctionInput1Schema(ValueString::class)) {
        override fun invoke(ctx: FunctionContext, arg1: ValueString) {
            result = arg1.stringValue
        }

        var result: String? = null
    }

    private class Captor2 : Function2In0Out<ValueString, ValueNumber>(
        FunctionInput2Schema(ValueString::class, ValueNumber::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: ValueString, arg2: ValueNumber) {
            result = "${arg1.stringValue}=${arg2.doubleValue}"
        }

        var result: String? = null
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop)
    }
}
