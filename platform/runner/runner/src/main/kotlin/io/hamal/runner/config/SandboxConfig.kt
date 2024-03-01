package io.hamal.runner.config

import io.hamal.extension.net.http.ExtensionHttpFactory
import io.hamal.extension.net.smtp.ExtensionSmtpFactory
import io.hamal.extension.std.decimal.ExtensionDecimalFactory
import io.hamal.extension.std.log.ExtensionLogFactory
import io.hamal.extension.telegram.ExtensionTelegramFactory
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Jar
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.plugin.net.http.PluginHttpFactory
import io.hamal.plugin.net.smtp.PluginSmtpFactory
import io.hamal.plugin.std.debug.PluginDebugFactory
import io.hamal.plugin.std.log.PluginLogFactory
import io.hamal.plugin.std.sys.PluginSysFactory
import io.hamal.plugin.web3.evm.PluginWeb3EthFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
open class SandboxConfig {
    @Bean
    open fun sandboxFactory(@Value("\${io.hamal.runner.api.host}") apiHost: String): SandboxFactory =
        SandboxFactoryDefaultImpl(apiHost)

    @Bean
    open fun envFactory(): EnvFactory = RunnerEnvFactoryDefaultImpl()
}

interface SandboxFactory {
    fun create(ctx: SandboxContext): Sandbox
}

interface EnvFactory {
    fun create(): RunnerEnv
}

class SandboxFactoryDefaultImpl(
    private val apiHost: String,
) : SandboxFactory {
    override fun create(ctx: SandboxContext): Sandbox {
        NativeLoader.load(Jar)

        val sdk = ApiSdkImpl(
            apiHost = apiHost,
            token = AuthToken("Bearer ${ctx[ExecToken::class].value}")
        )

        return Sandbox(ctx)
            .registerPlugins(
                PluginSmtpFactory(),
                PluginHttpFactory(),
                PluginLogFactory(sdk.execLog),
                PluginDebugFactory(),
                PluginSysFactory(sdk),
                PluginWeb3EthFactory()
            )
            .registerExtensions(
                ExtensionDecimalFactory,
                ExtensionSmtpFactory,
                ExtensionHttpFactory,
                ExtensionLogFactory,
                ExtensionTelegramFactory
            )
    }
}


class RunnerEnvFactoryDefaultImpl : EnvFactory {
    override fun create(): RunnerEnv {
        return RunnerEnv()
    }
}