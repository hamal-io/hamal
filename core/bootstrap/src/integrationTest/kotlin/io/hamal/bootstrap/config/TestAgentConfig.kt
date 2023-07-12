package io.hamal.bootstrap.config

import io.hamal.agent.extension.std.log.StdLogExtension
import io.hamal.agent.extension.std.sys.StdSysExtension
import io.hamal.bootstrap.TestExtension
import io.hamal.bootstrap.httpTemplate
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.api.value.TableValue
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import io.hamal.lib.sdk.HttpTemplateSupplier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestAgentConfig {
    @Bean
    fun httpTemplateSupplier(): HttpTemplateSupplier = { httpTemplate }

    @Bean
    fun envValue(): EnvValue {
        val extensionEnvironments = mutableListOf<EnvValue>()
        extensionEnvironments.add(StdLogExtension().create())
        extensionEnvironments.add(StdSysExtension(httpTemplateSupplier()).create())
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