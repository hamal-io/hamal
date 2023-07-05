package io.hamal.agent.config

import io.hamal.agent.adapter.ExtensionLoader
import io.hamal.agent.extension.std.log.StdLogExtension
import io.hamal.agent.extension.std.sys.StdSysExtension
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.api.value.TableValue
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.ExitFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.io.File

@Configuration
@Profile("!test")
open class EnvConfig {
    @Bean
    open fun envValue(): EnvValue {
        val extensionEnvironments = mutableListOf<EnvValue>()
        extensionEnvironments.add(StdLogExtension().create())
        extensionEnvironments.add(StdSysExtension().create())

        val entryPointLoader = ExtensionLoader.DefaultImpl()

        val web3 =
            entryPointLoader.load(File("/home/ddymke/Repo/hamal/agent/extension/web3/build/libs/extension-web3.jar"))
        extensionEnvironments.add(web3.create()) // FIXME store the factory not the environment - a new environment must be created when calling require each times



        return EnvValue(
            ident = IdentValue("_G"),
            values = TableValue(
                "assert" to AssertFunction,
                "exit" to ExitFunction,
                "require" to RequireFunction
            )
        ).apply {
            extensionEnvironments.forEach { environment ->
                addGlobal(environment.ident, environment)
            }
        }
    }

}