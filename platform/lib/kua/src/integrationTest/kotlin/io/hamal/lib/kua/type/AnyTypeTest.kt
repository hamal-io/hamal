package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class AnyTypeTest {

    @Test
    fun `AnyValue can be used with BooleanType`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(capability(captor))

        sandbox.load(
            """
            test = require_plugin('test')
            test.captor(test.pass_through(true))
        """
        )
        assertThat(captor.result, equalTo(AnyType(True)))
    }

    @Test
    fun `AnyValue can be used with NumberType`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(capability(captor))

        sandbox.load(
            """
            test = require_plugin('test')
            test.captor(test.pass_through(23))
        """
        )

        assertThat(captor.result, equalTo(AnyType(NumberType(23))))
    }

    @Test
    fun `AnyValue can be used with StringType`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(capability(captor))

        sandbox.load(
            """
            test = require_plugin('test')
            test.captor(test.pass_through('hamal.io'))
        """.trimIndent()
        )

        assertThat(captor.result, equalTo(AnyType(StringType("hamal.io"))))
    }

    @Test
    fun `AnyValue can be used with MapType`() {
        val map = sandbox.tableCreateMap(2)
        map["key"] = "value"
        sandbox.setGlobal("test_map", map)

        val captor = AnyValueResultCaptor()
        sandbox.register(capability(captor))

        sandbox.load(
            """
            test = require_plugin('test')
            test.captor(test.pass_through(test_map))
        """
        )

        val underlying = (captor.result as AnyType).value
        require(underlying is MapType) { "Not a MapType" }
        assertThat(underlying.size, equalTo(1))
        assertThat(underlying.getString("key"), equalTo("value"))
    }


    @Test
    fun `AnyValue can be used with ArrayType`() {
        val array = sandbox.tableCreateArray(2)
        array.append(23)
        array.append("hamal.io")
        sandbox.setGlobal("test_array", array)

        val captor = AnyValueResultCaptor()
        sandbox.register(capability(captor))

        sandbox.load(
            """
            test = require_plugin('test')
            test.captor(test.pass_through(test_array))
        """
        )

        val underlying = (captor.result as AnyType).value
        require(underlying is ArrayType) { "Not a ArrayType" }
        assertThat(underlying.size, equalTo(2))

        assertThat(underlying.getInt(1), equalTo(23))
        assertThat(underlying.getString(2), equalTo("hamal.io"))
    }

    private class AnyValuePassThrough : Function1In1Out<AnyType, AnyType>(
        FunctionInput1Schema(AnyType::class),
        FunctionOutput1Schema(AnyType::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: AnyType): AnyType {
            return arg1
        }
    }

    private class AnyValueResultCaptor : Function1In0Out<AnyType>(
        FunctionInput1Schema(AnyType::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: AnyType) {
            result = arg1
        }

        var result: Type = NilType
    }

    private fun capability(captor: FunctionType<*, *, *, *>) =
        RunnerPlugin(
            name = "test",
            factoryCode = """
                    function extension()
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