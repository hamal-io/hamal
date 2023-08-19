package io.hamal.lib.kua.table

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.error.ScriptErrorTest
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test


internal class TableEntryIteratorTest {

    @Test
    fun `Iterate over table map`() {
        class TestIteratorFunction : Function1In0Out<TableTypeMap>(
            FunctionInput1Schema(TableTypeMap::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableTypeMap) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getStringType(index) },
                    valueExtractor = { state, index -> state.getAny(index) }
                )

                val resultCollector = mutableMapOf<StringType, AnyType>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, hasSize(3))
                assertThat(resultCollector[StringType("key")], equalTo(AnyType(StringType("value"))))
                assertThat(resultCollector[StringType("answer")], equalTo(AnyType(NumberType(42))))
                assertThat(resultCollector[StringType("result")], equalTo(AnyType(TrueValue)))
            }
        }

        sandbox.register(NativeExtension("test", mapOf("invoke" to TestIteratorFunction())))
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
        class TestIteratorFunction : Function1In0Out<TableTypeMap>(
            FunctionInput1Schema(TableTypeMap::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableTypeMap) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getStringType(index) },
                    valueExtractor = { state, index -> state.getAny(index) }
                )

                val resultCollector = mutableMapOf<StringType, AnyType>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, empty())
            }
        }

        sandbox.register(NativeExtension("test", mapOf("invoke" to TestIteratorFunction())))
        sandbox.load(
            """
            local table = { }
            
            test.invoke(table)
        """.trimIndent()
        )
    }

    @Test
    fun `Iterate over table array`() {
        class TestIteratorFunction : Function1In0Out<TableTypeArray>(
            FunctionInput1Schema(TableTypeArray::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableTypeArray) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getNumberValue(index) },
                    valueExtractor = { state, index -> state.getAny(index) }
                )

                val resultCollector = mutableMapOf<NumberType, AnyType>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, hasSize(3))
                assertThat(resultCollector[NumberType(1)], equalTo(AnyType(StringType("value"))))
                assertThat(resultCollector[NumberType(2)], equalTo(AnyType(NumberType(42))))
                assertThat(resultCollector[NumberType(3)], equalTo(AnyType(TrueValue)))
            }
        }

        sandbox.register(NativeExtension("test", mapOf("invoke" to TestIteratorFunction())))
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
    fun `Iterate over nested table array`() {
        class TestIteratorFunction : Function1In0Out<TableTypeArray>(
            FunctionInput1Schema(TableTypeArray::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableTypeArray) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getNumberValue(index) },
                    valueExtractor = { state, index -> state.toTableType(state.getTableMap(index)) }
                )

                val resultCollector = mutableMapOf<NumberType, TableType>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }
                assertThat(resultCollector.keys, hasSize(3))

                val first = resultCollector[NumberType(1)]!!
                assertThat(first.getStringType("type"), equalTo(StringType("call")))

                val second = resultCollector[NumberType(2)]!!
                assertThat(second.getStringType("type"), equalTo(StringType("get_block")))

                val third = resultCollector[NumberType(3)]!!
                assertThat(third.getStringType("type"), equalTo(StringType("version")))
            }
        }

        sandbox.register(NativeExtension("test", mapOf("invoke" to TestIteratorFunction())))
        sandbox.load(
            """
            local table = {
                { type = 'call' },
                { type = 'get_block' },
                { type = 'version' }
            }
            test.invoke(table)
        """.trimIndent()
        )
    }

    @Test
    fun `Iterate over empty table array`() {
        class TestIteratorFunction : Function1In0Out<TableTypeArray>(
            FunctionInput1Schema(TableTypeArray::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableTypeArray) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getNumberValue(index) },
                    valueExtractor = { state, index -> state.getAny(index) }
                )

                val resultCollector = mutableMapOf<NumberType, AnyType>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, empty())
            }
        }

        sandbox.register(NativeExtension("test", mapOf("invoke" to TestIteratorFunction())))
        sandbox.load(
            """
            local table = {}
            test.invoke(table)
        """.trimIndent()
        )
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(NopSandboxContext()).also {
            it.register(NativeExtension("test", mapOf("call" to ScriptErrorTest.CallbackFunction())))
        }
    }
}