package io.hamal.lib.kua.value

import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.ResourceLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableArrayProxyValue
import io.hamal.lib.kua.table.TableLength
import io.hamal.lib.kua.table.TableMapProxyValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
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

        sandbox.runCode("test.captor(test.pass_through(true))")
        assertThat(captor.result, equalTo(AnyValue(TrueValue)))
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

        sandbox.runCode("test.captor(test.pass_through(23))")

        assertThat(captor.result, equalTo(AnyValue(NumberValue(23))))
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

        sandbox.runCode("test.captor(test.pass_through(\"hamal.io\"))")

        assertThat(captor.result, equalTo(AnyValue(StringValue("hamal.io"))))
    }

    @Test
    fun `AnyValue can be used with TableMapValue`() {
        val map = sandbox.tableCreateMap(2)
        map["key"] = "value"
        sandbox.setGlobal("test_map", map)

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

        sandbox.runCode("test.captor(test.pass_through(test_map))")

        val underlying = (captor.result as AnyValue).value
        require(underlying is TableMapProxyValue) { "Not a TableMapProxyValue" }
        assertThat(underlying.length(), equalTo(TableLength(1)))
        assertThat(underlying.getString("key"), equalTo("value"))
    }


    @Test
    fun `AnyValue can be used with TableArrayValue`() {
        val array = sandbox.tableCreateArray(2)
        array.append(23)
        array.append("hamal.io")
        sandbox.setGlobal("test_array", array)

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

        sandbox.runCode("test.captor(test.pass_through(test_array))")

        val underlying = (captor.result as AnyValue).value
        require(underlying is TableArrayProxyValue) { "Not a TableArrayProxyValue" }
        assertThat(underlying.length(), equalTo(TableLength(2)))

        assertThat(underlying.getInt(1), equalTo(23))
        assertThat(underlying.getString(2), equalTo("hamal.io"))
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