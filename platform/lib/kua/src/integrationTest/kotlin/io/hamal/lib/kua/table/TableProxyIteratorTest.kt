package io.hamal.lib.kua.table

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.error.ScriptErrorTest
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
        class TestIteratorFunction : Function1In0Out<TableProxyMap>(
            FunctionInput1Schema(TableProxyMap::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableProxyMap) {
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
                assertThat(resultCollector[StringType("result")], equalTo(AnyType(True)))
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function extension()
                        local internal = _internal
                        return function()
                            local export = { 
                                invoke =  internal.invoke,
                            }
                            return export
                        end
                    end
                """.trimIndent(),
                internals = mapOf("invoke" to TestIteratorFunction())
            )
        )
        sandbox.load(
            """
            test = require('test')
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
        class TestIteratorFunction : Function1In0Out<TableProxyMap>(
            FunctionInput1Schema(TableProxyMap::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableProxyMap) {
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

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function extension()
                        local internal = _internal
                        return function()
                            local export = { 
                                invoke =  internal.invoke,
                            }
                            return export
                        end
                    end
                """.trimIndent(),
                internals = mapOf("invoke" to TestIteratorFunction())
            )
        )
        sandbox.load(
            """
            test = require('test')
            local table = { }
            
            test.invoke(table)
        """.trimIndent()
        )
    }

    @Test
    fun `Iterate over table array`() {
        class TestIteratorFunction : Function1In0Out<TableProxyArray>(
            FunctionInput1Schema(TableProxyArray::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableProxyArray) {
                val testInstance = TableEntryIterator(-1, ctx,
                    keyExtractor = { state, index -> state.getNumberType(index) },
                    valueExtractor = { state, index -> state.getAny(index) }
                )

                val resultCollector = mutableMapOf<NumberType, AnyType>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, hasSize(3))
                assertThat(resultCollector[NumberType(1)], equalTo(AnyType(StringType("value"))))
                assertThat(resultCollector[NumberType(2)], equalTo(AnyType(NumberType(42))))
                assertThat(resultCollector[NumberType(3)], equalTo(AnyType(True)))
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function extension()
                        local internal = _internal
                        return function()
                            local export = { 
                                invoke =  internal.invoke,
                            }
                            return export
                        end
                    end
                """.trimIndent(),
                internals = mapOf("invoke" to TestIteratorFunction())
            )
        )
        sandbox.load(
            """
            test = require('test')
            
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
        class TestIteratorFunction : Function1In0Out<TableProxyArray>(
            FunctionInput1Schema(TableProxyArray::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableProxyArray) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getNumberType(index) },
                    valueExtractor = { state, index -> state.toMapType(state.getTableMapProxy(index)) }
                )

                val resultCollector = mutableMapOf<NumberType, MapType>()
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

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function extension()
                        local internal = _internal
                        return function()
                            local export = { 
                                invoke =  internal.invoke,
                            }
                            return export
                        end
                    end
                """.trimIndent(),
                internals = mapOf("invoke" to TestIteratorFunction())
            )
        )
        sandbox.load(
            """
            test = require('test')
                
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
        class TestIteratorFunction : Function1In0Out<TableProxyArray>(
            FunctionInput1Schema(TableProxyArray::class)
        ) {
            override fun invoke(ctx: FunctionContext, arg1: TableProxyArray) {
                val testInstance = TableEntryIterator(
                    -1,
                    ctx,
                    keyExtractor = { state, index -> state.getNumberType(index) },
                    valueExtractor = { state, index -> state.getAny(index) }
                )

                val resultCollector = mutableMapOf<NumberType, AnyType>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, empty())
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function extension()
                        local internal = _internal
                        return function()
                            local export = { 
                                invoke =  internal.invoke,
                            }
                            return export
                        end
                    end
                """.trimIndent(),
                internals = mapOf("invoke" to TestIteratorFunction())
            )
        )
        sandbox.load(
            """
            test = require('test')
            local table = {}
            test.invoke(table)
        """.trimIndent()
        )
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(NopSandboxContext()).also {
            it.register(
                RunnerPlugin(
                    name = "test",
                    factoryCode = """
                    function extension()
                        local internal = _internal
                        return function()
                            local export = { 
                                call =  internal.call,
                            }
                            return export
                        end
                    end
                """.trimIndent(),
                    internals = mapOf("call" to ScriptErrorTest.CallbackFunction())
                )
            )
        }
    }
}