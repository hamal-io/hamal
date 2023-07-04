package io.hamal.agent.config

import io.hamal.agent.extension.std.log.StdLogExtension
import io.hamal.agent.extension.std.sys.StdSysExtension
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("default")
open class EnvConfig {
    @Bean
    open fun envValue(): EnvValue {
        val extensionEnvironments = mutableListOf<EnvValue>()
        extensionEnvironments.add(StdLogExtension().create())
        extensionEnvironments.add(StdSysExtension().create())

        return EnvValue(
            ident = IdentValue("_G"),
            values = mapOf(
                IdentValue("assert") to AssertFunction,
                IdentValue("require") to RequireFunction
            )
        ).apply {
            extensionEnvironments.forEach { environment ->
                addGlobal(environment.ident, environment)
            }
        }
    }

}