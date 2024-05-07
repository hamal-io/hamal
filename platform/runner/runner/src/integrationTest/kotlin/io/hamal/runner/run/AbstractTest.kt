package io.hamal.runner.run

import io.hamal.lib.common.value.Value
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.test.RunnerFixture
import io.hamal.runner.test.TestConnector

internal abstract class AbstractTest {

    fun runTest(
        unitOfWork: UnitOfWork,
        connector: Connector = TestConnector(),
        testPlugins: Map<ValueString, Value> = mapOf(),
    ): CodeRunnerImpl {
        return RunnerFixture.createTestRunner(
            connector = connector,
            pluginFactories = listOf(RunnerPluginFactory {
                RunnerPlugin(
                    name = ValueString("test"),
                    factoryCode = ValueCode(
                        """
                            function plugin_create(internal)
                                local export = {
                                    ${testPlugins.keys.joinToString(",") { plugin -> "$plugin = internal.${plugin}" }}
                                 }
                                return export
                            end
                            """.trimIndent()
                    ),
                    internals = testPlugins
                )
            })
        ).also { runner -> runner.run(unitOfWork) }
    }

}