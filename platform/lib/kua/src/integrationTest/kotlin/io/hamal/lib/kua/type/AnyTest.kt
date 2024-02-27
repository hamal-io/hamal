package io.hamal.lib.kua.type

import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class KuaAnyTest {

    @Test
    fun `Can be used with boolean`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.load(
            """
            test = require_plugin('test')
            test.captor(test.pass_through(true))
        """
        )
        assertThat(captor.result, equalTo(KuaAny(KuaTrue)))
    }

    @Test
    fun `Can be used with number`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.load(
            """
            test = require_plugin('test')
            test.captor(test.pass_through(23))
        """
        )

        assertThat(captor.result, equalTo(KuaAny(KuaNumber(23))))
    }

    @Test
    fun `Can be used with string`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.load(
            """
            test = require_plugin('test')
            test.captor(test.pass_through('hamal.io'))
        """.trimIndent()
        )

        assertThat(captor.result, equalTo(KuaAny(KuaString("hamal.io"))))
    }

    @Test
    fun `Can be used with table object style table`() {
        val map = sandbox.tableCreateMap(2)
        map["key"] = "value"
        sandbox.setGlobal("test_map", map)

        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.load(
            """
            test = require_plugin('test')
            test.captor(test.pass_through(test_map))
        """
        )

        val underlying = (captor.result as KuaAny).value
        require(underlying is KuaTable) { "Not a table" }
        assertThat(underlying.length, equalTo(1))
        assertThat(underlying.getString("key"), equalTo("value"))
    }


    @Test
    fun `AnyValue can be used with array style table`() {
        val array = sandbox.tableCreateArray(2)
        array.append(23)
        array.append("hamal.io")
        sandbox.setGlobal("test_array", array)

        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.load(
            """
            test = require_plugin('test')
            test.captor(test.pass_through(test_array))
        """
        )

        val underlying = (captor.result as KuaAny).value
        require(underlying is KuaTable) { "Not a array" }
        assertThat(underlying.length, equalTo(2))

        assertThat(underlying.getInt(1), equalTo(23))
        assertThat(underlying.getString(2), equalTo("hamal.io"))
    }

    private class AnyValuePassThrough : Function1In1Out<KuaAny, KuaAny>(
        FunctionInput1Schema(KuaAny::class),
        FunctionOutput1Schema(KuaAny::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: KuaAny): KuaAny {
            return arg1
        }
    }

    private class AnyValueResultCaptor : Function1In0Out<KuaAny>(
        FunctionInput1Schema(KuaAny::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: KuaAny) {
            result = arg1
        }

        var result: KuaType = KuaNil
    }

    private fun plugin(captor: KuaFunction<*, *, *, *>) =
        RunnerPlugin(
            name = "test",
            factoryCode = """
                    function plugin()
                        local internal = _internal
                        return function()
                            local export = { 
                                pass_through =  internal.pass_through,
                                captor =  internal.captor
                            }
                            return export
                        end
                    end
                """.trimIndent(),
            internals = mapOf(
                "pass_through" to AnyValuePassThrough(),
                "captor" to captor
            )
        )


    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(NopSandboxContext())
    }
}