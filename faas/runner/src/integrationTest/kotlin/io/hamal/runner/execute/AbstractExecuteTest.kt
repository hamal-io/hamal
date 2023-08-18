package io.hamal.runner.execute

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.type.Type
import io.hamal.runner.StringCaptor
import io.hamal.runner.TestConnector
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector

internal abstract class AbstractExecuteTest(
    val stringCaptor: StringCaptor = StringCaptor()
) {

    fun createTestExecutor(
        vararg testExtensions: Pair<String, Type>,
        connector: Connector = TestConnector()
    ) = DefaultExecutor(
        connector, object : SandboxFactory {
            override fun create(ctx: SandboxContext): Sandbox {
                NativeLoader.load(Resources)
                return Sandbox(ctx).also {
                    it.register(
                        NativeExtension(
                            name = "test",
                            values = mapOf(
                                "capture_string" to stringCaptor
                            ).plus(testExtensions)
                        )
                    )
                }
            }
        }
    )


}