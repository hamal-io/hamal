package io.hamal.extension.std.once

import io.hamal.extension.std.table.ExtensionStdTableFactory
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.TestConnector

internal abstract class AbstractTest {

    fun runTest(unitOfWork: UnitOfWork, connector: Connector = TestConnector()) {
        createTestRunner(
            connector = connector,
            extensionFactories = listOf(ExtensionStdTableFactory, ExtensionStdOnceFactory),
            pluginFactories = listOf(RunnerPluginFactory {
                RunnerPlugin(
                    name = ValueString("test"),
                    factoryCode = ValueCode(
                        """
                        function plugin_create(internal)
                            local export = {
                                invoke = internal.invoke,
                            }
                            return export
                        end
                """.trimIndent()
                    ),
                    internals = mapOf(ValueString("invoke") to InvokeFunction)
                )
            }),
        ).run(unitOfWork)
    }


    data object InvokeFunction : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            invocations++
        }

        var invocations: Int = 0
    }
}