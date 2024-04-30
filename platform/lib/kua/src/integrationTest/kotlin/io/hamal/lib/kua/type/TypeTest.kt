package io.hamal.lib.kua.type

import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.*
import io.hamal.lib.value.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class ValueTest {

    @Test
    fun `Can be used with boolean`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.codeLoad(
            ValueCode(
                """
            test = require_plugin('test')
            test.captor(test.pass_through(true))
        """
            )
        )
        assertThat(captor.result, equalTo(ValueTrue))
    }

    @Test
    fun `Can be used with number`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.codeLoad(
            ValueCode(
                """
            test = require_plugin('test')
            test.captor(test.pass_through(23))
        """
            )
        )

        assertThat(captor.result, equalTo(ValueNumber(23)))
    }

    @Test
    fun `Can be used with string`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.codeLoad(
            ValueCode(
                """
            test = require_plugin('test')
            test.captor(test.pass_through('hamal.io'))
        """.trimIndent()
            )
        )

        assertThat(captor.result, equalTo(ValueString("hamal.io")))
    }

    @Test
    fun `Can be used with table`() {
        val map = sandbox.tableCreate(0, 2)
        map["key"] = ValueString("value")
        sandbox.globalSet(ValueString("test_map"), map)

        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.codeLoad(
            ValueCode(
                """
            test = require_plugin('test')
            test.captor(test.pass_through(test_map))
        """
            )
        )

        val underlying = captor.result
        require(underlying is KuaTable) { "Not a table" }
        assertThat(underlying.length, equalTo(TableLength(1)))
        assertThat(underlying.getString("key"), equalTo(ValueString("value")))
    }

    private class AnyValuePassThrough : Function1In1Out<Value, Value>(
        FunctionInput1Schema(Value::class),
        FunctionOutput1Schema(Value::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: Value): Value {
            return arg1
        }
    }

    private class AnyValueResultCaptor : Function1In0Out<Value>(
        FunctionInput1Schema(Value::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: Value) {
            result = arg1
        }

        var result: Value = ValueNil
    }

    private fun plugin(captor: KuaFunction<*, *, *, *>) =
        RunnerPlugin(
            name = ValueString("test"),
            factoryCode = ValueCode(
                """
                    function plugin_create(internal)
                        local export = { 
                            pass_through =  internal.pass_through,
                            captor =  internal.captor
                        }
                        return export
                    end
                """.trimIndent()
            ),
            internals = mapOf(
                ValueString("pass_through") to AnyValuePassThrough(),
                ValueString("captor") to captor
            )
        )


    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop)
    }
}