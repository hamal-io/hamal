package io.hamal.bootstrap.config

import io.hamal.bootstrap.TestExtensionFactory
import io.hamal.extension.std.log.LogExtensionFactory
import io.hamal.extension.std.sys.DepSysExtensionFactory
import io.hamal.extension.std.sys.SysExtensionFactory
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxFactory
import io.hamal.lib.sdk.HttpTemplateSupplier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestAgentConfig {
    @Bean
    fun httpTemplateSupplier(): HttpTemplateSupplier = { HttpTemplate("http://localhost:8042") }

    @Bean
    fun sandboxFactory(): SandboxFactory = object : SandboxFactory {
        override fun create(): Sandbox {
            NativeLoader.load(Resources)
            val result = Sandbox()
            result.register(TestExtensionFactory().create())
            result.register(DepSysExtensionFactory(httpTemplateSupplier()).create())
            result.register(LogExtensionFactory().create())
            result.register(SysExtensionFactory(httpTemplateSupplier()).create())
            return result
        }
    }

}