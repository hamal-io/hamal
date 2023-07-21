package io.hamal.agent.config

import io.hamal.lib.kua.FixedPathLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
open class SandboxConfig {

    private val instance = run {
        FixedPathLoader.load()
        Sandbox()
    }

    @Bean
    open fun sandboxFactory(): SandboxFactory = object : SandboxFactory {
        override fun create(): Sandbox = instance
    }
}