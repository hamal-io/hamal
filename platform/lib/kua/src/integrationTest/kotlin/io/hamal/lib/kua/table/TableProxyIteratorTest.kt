package io.hamal.lib.kua.table

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.error.ScriptErrorTest
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
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

                val resultCollector = mutableMapOf<KuaString, KuaAny>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, hasSize(3))
                assertThat(resultCollector[KuaString("key")], equalTo(KuaAny(KuaString("value"))))
                assertThat(resultCollector[KuaString("answer")], equalTo(KuaAny(KuaNumber(42))))
                assertThat(resultCollector[KuaString("result")], equalTo(KuaAny(KuaTrue)))
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function plugin()
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
            test = require_plugin('test')
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

                val resultCollector = mutableMapOf<KuaString, KuaAny>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, empty())
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function plugin()
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
            test = require_plugin('test')
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

                val resultCollector = mutableMapOf<KuaNumber, KuaAny>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, hasSize(3))
                assertThat(resultCollector[KuaNumber(1)], equalTo(KuaAny(KuaString("value"))))
                assertThat(resultCollector[KuaNumber(2)], equalTo(KuaAny(KuaNumber(42))))
                assertThat(resultCollector[KuaNumber(3)], equalTo(KuaAny(KuaTrue)))
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function plugin()
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
            test = require_plugin('test')
            
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
                    valueExtractor = { state, index -> state.toKuaMap(state.getTableMapProxy(index)) }
                )

                val resultCollector = mutableMapOf<KuaNumber, KuaMap>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }
                assertThat(resultCollector.keys, hasSize(3))

                val first = resultCollector[KuaNumber(1)]!!
                assertThat(first.getStringType("type"), equalTo(KuaString("call")))

                val second = resultCollector[KuaNumber(2)]!!
                assertThat(second.getStringType("type"), equalTo(KuaString("get_block")))

                val third = resultCollector[KuaNumber(3)]!!
                assertThat(third.getStringType("type"), equalTo(KuaString("version")))
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function plugin()
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
            test = require_plugin('test')
                
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

                val resultCollector = mutableMapOf<KuaNumber, KuaAny>()
                testInstance.forEach { entry -> resultCollector[entry.key] = entry.value }

                assertThat(resultCollector.keys, empty())
            }
        }

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function plugin()
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
            test = require_plugin('test')
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
                    function plugin()
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