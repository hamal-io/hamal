package io.hamal.runner.run

import TestConnector
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.type.Type
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector

internal abstract class AbstractExecuteTest {
    fun createTestRunner(
        vararg testExtensions: Pair<String, Type>,
        connector: Connector = TestConnector()
    ) = CodeRunnerImpl(
        connector, object : SandboxFactory {
            override fun create(ctx: SandboxContext): Sandbox {
                NativeLoader.load(Resources)
                return Sandbox(ctx).also {
                    it.register(
                        NativeExtension(
                            name = "test",
                            values = testExtensions.toMap()
                        )
                    )
                }
            }
        }
    )
}