package io.hamal.bootstrap.config

import io.hamal.agent.extension.std.log.LogExtensionFactory
import io.hamal.agent.extension.std.sys.SysExtensionFactory
import io.hamal.bootstrap.TestExtensionFactory
import io.hamal.bootstrap.httpTemplate
import io.hamal.lib.kua.FixedPathLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxFactory
import io.hamal.lib.sdk.HttpTemplateSupplier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestAgentConfig {
    @Bean
    fun httpTemplateSupplier(): HttpTemplateSupplier = { httpTemplate }

    @Bean
    fun sandboxFactory(): SandboxFactory = object : SandboxFactory {
        override fun create(): Sandbox {
//            ResourceLoader.load()
            FixedPathLoader.load()
            val result = Sandbox()
            result.register(TestExtensionFactory().create())
            result.register(SysExtensionFactory(httpTemplateSupplier()).create())
            result.register(LogExtensionFactory().create())
            return result
        }
    }

}