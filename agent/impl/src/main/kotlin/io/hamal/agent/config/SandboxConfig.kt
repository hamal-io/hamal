package io.hamal.agent.config

import io.hamal.agent.extension.api.ExtensionFuncInvocationContext
import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.FuncInvocationContextFactory
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.DefaultSandbox
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SandboxConfig {
    @Bean
    open fun sandbox(envValue: EnvValue): Sandbox = DefaultSandbox(
        envValue,
        object : FuncInvocationContextFactory<ExtensionFuncInvocationContext> {
            override fun create(
                parameters: List<Value>,
                env: EnvValue
            ): ExtensionFuncInvocationContext {
                return ExtensionFuncInvocationContext(parameters, env)
            }
        })

}