package io.hamal.runner.run

import io.hamal.extension.std.table.ExtensionStdTableFactory
import io.hamal.extension.std.`throw`.ExtensionStdThrowFactory
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.value.KuaValue
import io.hamal.runner.config.EnvFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.test.TestConnector

internal abstract class AbstractExecuteTest {
    fun createTestRunner(
        vararg testPlugins: Pair<ValueString, KuaValue>,
        connector: Connector = TestConnector()
    ) = CodeRunnerImpl(
        connector,
        object : SandboxFactory {
            override fun create(ctx: SandboxContext): Sandbox {
                NativeLoader.load(Resources)
                return Sandbox(ctx)
                    .also { sandbox -> sandbox.registerExtensions(ExtensionStdTableFactory, ExtensionStdThrowFactory) }
                    .also { sandbox ->
                        sandbox.register(
                            RunnerPlugin(
                                name = ValueString("test"),
                                factoryCode = ValueCode(
                                    """
                            function plugin_create(internal)
                                local export = {
                                    ${testPlugins.joinToString(",") { plugin -> "${plugin.first} = internal.${plugin.first}" }}
                                 }
                                return export
                            end
                            """.trimIndent()
                                ),
                                internals = testPlugins.toMap()
                            )
                        )
                    }
            }
        },
        object : EnvFactory {
            override fun create() = RunnerEnv()
        }
    )
}