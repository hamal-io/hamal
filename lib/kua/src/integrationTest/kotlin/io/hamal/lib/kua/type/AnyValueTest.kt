package io.hamal.lib.kua.type

import io.hamal.lib.kua.DefaultSandboxContext
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableArray
import io.hamal.lib.kua.table.TableMap
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class AnyValueTest {

    @Test
    fun `AnyValue can be used with BooleanValue`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "pass_through" to AnyValuePassThrough(),
                    "captor" to captor
                )
            )
        )

        sandbox.load("test.captor(test.pass_through(true))")
        assertThat(captor.result, equalTo(AnyType(TrueValue)))
    }

    @Test
    fun `AnyValue can be used with NumberValue`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "pass_through" to AnyValuePassThrough(),
                    "captor" to captor
                )
            )
        )

        sandbox.load("test.captor(test.pass_through(23))")

        assertThat(captor.result, equalTo(AnyType(NumberType(23))))
    }

    @Test
    fun `AnyValue can be used with StringValue`() {
        val captor = AnyValueResultCaptor()
        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "pass_through" to AnyValuePassThrough(),
                    "captor" to captor
                )
            )
        )

        sandbox.load("test.captor(test.pass_through(\"hamal.io\"))")

        assertThat(captor.result, equalTo(AnyType(StringType("hamal.io"))))
    }

    @Test
    fun `AnyValue can be used with TableMapValue`() {
        val map = sandbox.tableCreateMap(2)
        map["key"] = "value"
        sandbox.setGlobal("test_map", map)

        val captor = AnyValueResultCaptor()
        sandbox.register(
            NativeExtension(
                name = "test",
                values = mapOf(
                    "pass_through" to AnyValuePassThrough(),
                    "captor" to captor
                )
            )
        )

        sandbox.load("test.captor(test.pass_through(test_map))")

        val underlying = (captor.result as AnyType).value
        require(underlying is TableMap) { "Not a TableMapProxyValue" }
        assertThat(underlying.length(), equalTo(1))
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
            NativeExtension(
                name = "test",
                values = mapOf(
                    "pass_through" to AnyValuePassThrough(),
                    "captor" to captor
                )
            )
        )

        sandbox.load("test.captor(test.pass_through(test_array))")

        val underlying = (captor.result as AnyType).value
        require(underlying is TableArray) { "Not a TableArrayProxyValue" }
        assertThat(underlying.length(), equalTo(2))

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

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(DefaultSandboxContext())
    }
}