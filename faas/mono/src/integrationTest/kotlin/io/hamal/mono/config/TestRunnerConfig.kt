package io.hamal.mono.config

import io.hamal.extension.std.log.LogExtensionFactory
import io.hamal.extension.std.sys.SysExtensionFactory
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.DefaultSandboxContext
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.sdk.HttpTemplateSupplier
import io.hamal.lib.sdk.domain.DequeueExecsResponse
import io.hamal.runner.config.SandboxFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestRunnerConfig {
    @Bean
    fun httpTemplateSupplier(): HttpTemplateSupplier = { HttpTemplate("http://localhost:8042") }

    @Bean
    fun sandboxFactory(): SandboxFactory = object : SandboxFactory {
        override fun create(exec: DequeueExecsResponse.Exec): Sandbox {
            NativeLoader.load(Resources)
            val result = Sandbox(DefaultSandboxContext())
            result.register(LogExtensionFactory(httpTemplateSupplier()).create())
            result.register(SysExtensionFactory(httpTemplateSupplier()).create())
            return result
        }
    }
}