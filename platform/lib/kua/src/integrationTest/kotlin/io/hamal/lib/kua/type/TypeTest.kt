package io.hamal.lib.kua.type

import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class KuaTypeTest {

    @Test
    fun `Can be used with boolean`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.codeLoad(
            KuaCode(
                """
            test = require_plugin('test')
            test.captor(test.pass_through(true))
        """
            )
        )
        assertThat(captor.result, equalTo(KuaTrue))
    }

    @Test
    fun `Can be used with number`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.codeLoad(
            KuaCode(
                """
            test = require_plugin('test')
            test.captor(test.pass_through(23))
        """
            )
        )

        assertThat(captor.result, equalTo(KuaNumber(23)))
    }

    @Test
    fun `Can be used with string`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.codeLoad(
            KuaCode(
                """
            test = require_plugin('test')
            test.captor(test.pass_through('hamal.io'))
        """.trimIndent()
            )
        )

        assertThat(captor.result, equalTo(KuaString("hamal.io")))
    }

    @Test
    fun `Can be used with table`() {
        val map = sandbox.tableCreate(0, 2)
        map["key"] = KuaString("value")
        sandbox.globalSet(KuaString("test_map"), map)

        val captor = AnyValueResultCaptor()
        sandbox.register(plugin(captor))

        sandbox.codeLoad(
            KuaCode(
                """
            test = require_plugin('test')
            test.captor(test.pass_through(test_map))
        """
            )
        )

        val underlying = captor.result
        require(underlying is KuaTable) { "Not a table" }
        assertThat(underlying.length, equalTo(TableLength(1)))
        assertThat(underlying.getString("key"), equalTo(KuaString("value")))
    }

    private class AnyValuePassThrough : Function1In1Out<KuaType, KuaType>(
        FunctionInput1Schema(KuaType::class),
        FunctionOutput1Schema(KuaType::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: KuaType): KuaType {
            return arg1
        }
    }

    private class AnyValueResultCaptor : Function1In0Out<KuaType>(
        FunctionInput1Schema(KuaType::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: KuaType) {
            result = arg1
        }

        var result: KuaType = KuaNil
    }

    private fun plugin(captor: KuaFunction<*, *, *, *>) =
        RunnerPlugin(
            name = KuaString("test"),
            factoryCode = KuaCode(
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
                KuaString("pass_through") to AnyValuePassThrough(),
                KuaString("captor") to captor
            )
        )


    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop)
    }
}