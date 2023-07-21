package io.hamal.bootstrap.config

import io.hamal.bootstrap.httpTemplate
import io.hamal.lib.kua.ResourceLoader
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
            val result = Sandbox(ResourceLoader)

            return result
        }
    }
}