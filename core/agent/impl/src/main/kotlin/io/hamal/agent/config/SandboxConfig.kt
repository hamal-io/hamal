package io.hamal.agent.config

import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.impl.DefaultSandbox
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SandboxConfig {
    @Bean
    open fun sandbox(envValue: EnvValue): Sandbox = DefaultSandbox(envValue)

}