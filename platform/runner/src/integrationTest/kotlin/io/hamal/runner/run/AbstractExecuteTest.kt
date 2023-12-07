package io.hamal.runner.run

import TestConnector
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.type.Type
import io.hamal.runner.config.EnvFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector

internal abstract class AbstractExecuteTest {
    fun createTestRunner(
        vararg testPlugins: Pair<String, Type>,
        connector: Connector = TestConnector()
    ) = CodeRunnerImpl(
        connector,
        object : SandboxFactory {
            override fun create(ctx: SandboxContext): Sandbox {
                NativeLoader.load(Resources)
                return Sandbox(ctx).also {
                    it.register(
                        RunnerPlugin(
                            name = "test",
                            factoryCode = """
                            function plugin()
                                local internal = _internal
                                return function()
                                    local export = {
                                        ${testPlugins.joinToString(",") { plugin -> "${plugin.first} = internal.${plugin.first}" }}
                                     }
                                    return export
                                end
                            end
                            """.trimIndent(),
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