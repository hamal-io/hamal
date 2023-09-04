package io.hamal.runner.config

import io.hamal.extension.net.http.HttpExtensionFactory
import io.hamal.extension.std.log.DecimalExtensionFactory
import io.hamal.extension.std.log.LogExtensionFactory
import io.hamal.extension.std.sys.SysExtensionFactory
import io.hamal.extension.web3.eth.EthExtensionFactory
import io.hamal.extension.web3.hml.HmlExtensionFactory
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Jar
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.DefaultHubSdk
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
//@Profile("!test")
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

        val execToken = ctx[ExecToken::class]
        println(execToken)

        val template = HttpTemplate(
            baseUrl = instanceHost,
            headerFactory = {
                set("x-runner-exec-token", execToken.value)
            }
        )
        val sdk = DefaultHubSdk(template)

        return Sandbox(ctx).register(
            DecimalExtensionFactory,
            HttpExtensionFactory(),
            LogExtensionFactory(sdk.execLog),
            SysExtensionFactory(template),
            EthExtensionFactory(),
            HmlExtensionFactory()
        )
    }
}