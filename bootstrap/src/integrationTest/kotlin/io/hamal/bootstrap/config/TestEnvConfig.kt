package io.hamal.bootstrap.config

import io.hamal.agent.extension.std.log.StdLogExtension
import io.hamal.agent.extension.std.sys.StdSysExtension
import io.hamal.bootstrap.TestExtension
import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class TestEnvConfig {
    @Bean
    fun envValue(): EnvValue {
        val extensionEnvironments = mutableListOf<EnvValue>()
        extensionEnvironments.add(StdLogExtension().create())
        extensionEnvironments.add(StdSysExtension().create())
        extensionEnvironments.add(TestExtension().create())

        return EnvValue(
            ident = IdentValue("_G"),
            values = TableValue(
                "assert" to AssertFunction,
                "require" to RequireFunction,
            )
        ).apply {
            extensionEnvironments.forEach { environment ->
                addGlobal(environment.ident, environment)
            }
        }
    }
}