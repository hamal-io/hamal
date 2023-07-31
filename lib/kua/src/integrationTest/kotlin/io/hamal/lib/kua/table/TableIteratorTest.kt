package io.hamal.lib.kua.table

import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.error.ScriptErrorTest
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.NamedFunctionValue
import io.hamal.lib.kua.value.AnyValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TrueValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test


internal class TableEntryIteratorTest {

    @Test
    fun `Iterate over table map`() {
        class TestIteratorFunction : Function1In0Out<TableMapValue>(
            FunctionInput1Schema(TableMapValue::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableMapValue) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getStringValue(index) },
                    valueExtractor = { state, index -> state.getAnyValue(index) }
                )

                val resultCollector = mutableMapOf<StringValue, AnyValue>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, hasSize(3))
                assertThat(resultCollector[StringValue("key")], equalTo(AnyValue(StringValue("value"))))
                assertThat(resultCollector[StringValue("answer")], equalTo(AnyValue(NumberValue(42))))
                assertThat(resultCollector[StringValue("result")], equalTo(AnyValue(TrueValue)))
            }
        }

        sandbox.register(Extension("test", listOf(NamedFunctionValue("invoke", TestIteratorFunction()))))
        sandbox.load(
            """
            local table = {
                key = 'value',
                answer = 42,
                result = true
            }
            
            test.invoke(table)
        """.trimIndent()
        )
    }

    @Test
    fun `Iterate over empty table map`() {
        class TestIteratorFunction : Function1In0Out<TableMapValue>(
            FunctionInput1Schema(TableMapValue::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableMapValue) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getStringValue(index) },
                    valueExtractor = { state, index -> state.getAnyValue(index) }
                )

                val resultCollector = mutableMapOf<StringValue, AnyValue>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, empty())
            }
        }

        sandbox.register(Extension("test", listOf(NamedFunctionValue("invoke", TestIteratorFunction()))))
        sandbox.load(
            """
            local table = { }
            
            test.invoke(table)
        """.trimIndent()
        )
    }

    @Test
    fun `Iterate over table array`() {
        class TestIteratorFunction : Function1In0Out<TableArrayValue>(
            FunctionInput1Schema(TableArrayValue::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableArrayValue) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getNumberValue(index) },
                    valueExtractor = { state, index -> state.getAnyValue(index) }
                )

                val resultCollector = mutableMapOf<NumberValue, AnyValue>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, hasSize(3))
                assertThat(resultCollector[NumberValue(1)], equalTo(AnyValue(StringValue("value"))))
                assertThat(resultCollector[NumberValue(2)], equalTo(AnyValue(NumberValue(42))))
                assertThat(resultCollector[NumberValue(3)], equalTo(AnyValue(TrueValue)))
            }
        }

        sandbox.register(Extension("test", listOf(NamedFunctionValue("invoke", TestIteratorFunction()))))
        sandbox.load(
            """
            local table = {
                [1] = 'value',
                [2] = 42,
                [3] = true
            }
            
            test.invoke(table)
        """.trimIndent()
        )
    }

    @Test
    fun `Iterate over empty table array`() {
        class TestIteratorFunction : Function1In0Out<TableArrayValue>(
            FunctionInput1Schema(TableArrayValue::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableArrayValue) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getNumberValue(index) },
                    valueExtractor = { state, index -> state.getAnyValue(index) }
                )

                val resultCollector = mutableMapOf<NumberValue, AnyValue>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, empty())
            }
        }

        sandbox.register(Extension("test", listOf(NamedFunctionValue("invoke", TestIteratorFunction()))))
        sandbox.load(
            """
            local table = {}
            test.invoke(table)
        """.trimIndent()
        )
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox().also {
            it.register(Extension("test", listOf(NamedFunctionValue("call", ScriptErrorTest.CallbackFunction()))))
        }
    }
}