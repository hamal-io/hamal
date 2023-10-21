package io.hamal.runner.run

import TestConnector
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.plugin.capability.Capability
import io.hamal.lib.kua.type.Type
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector

internal abstract class AbstractExecuteTest {
    fun createTestRunner(
        vararg testCapabilities: Pair<String, Type>,
        connector: Connector = TestConnector()
    ) = CodeRunnerImpl(
        connector, object : SandboxFactory {
            override fun create(ctx: SandboxContext): Sandbox {
                NativeLoader.load(Resources)
                return Sandbox(ctx).also {
                    it.register(
                        Capability(
                            name = "test",
                            factoryCode = """
                            function plugin()
                                local internal = _internal
                                return function()
                                    local export = {
                                        ${
                                testCapabilities.joinToString(",") { cap -> "${cap.first} = internal.${cap.first}" }
                            }
                                     }
                                    return export
                                end
                            end
                            """.trimIndent(),
                            internals = testCapabilities.toMap()
                        )
                    )
                }
            }
        }
    )
}