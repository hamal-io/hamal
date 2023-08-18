package io.hamal.runner.config

import io.hamal.extension.std.log.LogExtensionFactory
import io.hamal.extension.std.sys.SysExtensionFactory
import io.hamal.extension.web3.eth.EthExtensionFactory
import io.hamal.extension.web3.hml.HmlExtensionFactory
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Jar
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class SandboxConfig {
    @Bean
    fun sandboxFactory(
        @Value("\${io.hamal.runner.host}") host: String
    ): SandboxFactory = RunnerSandboxFactory(host)
}

interface SandboxFactory {
    fun create(ctx: SandboxContext): Sandbox
}

class RunnerSandboxFactory(
    val instanceHost: String
) : SandboxFactory {
    override fun create(ctx: SandboxContext): Sandbox {
        NativeLoader.load(Jar)

        val template = HttpTemplate(instanceHost) // FIXME sdk instead

        return Sandbox(ctx).also {
            it.register(LogExtensionFactory { template }.create())
            it.register(SysExtensionFactory { template }.create())
            it.register(EthExtensionFactory().create())
            it.register(HmlExtensionFactory().create())
        }
    }

}