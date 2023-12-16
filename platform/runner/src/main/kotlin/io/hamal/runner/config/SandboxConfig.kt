package io.hamal.runner.config

import io.hamal.extension.net.smtp.SmtpExtensionFactory
import io.hamal.extension.net.http.HttpExtensionFactory
import io.hamal.extension.std.decimal.DecimalExtensionFactory
import io.hamal.extension.std.log.LogExtensionFactory
import io.hamal.extension.telegram.TelegramExtensionFactory
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Jar
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.plugin.net.smtp.SmtpPluginFactory
import io.hamal.plugin.net.http.HttpPluginFactory
import io.hamal.plugin.std.debug.DebugPluginFactory
import io.hamal.plugin.std.log.LogPluginFactory
import io.hamal.plugin.std.sys.SysPluginFactory
import io.hamal.plugin.web3.evm.EthPluginFactory
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
                SmtpPluginFactory(),
                HttpPluginFactory(),
                LogPluginFactory(sdk.execLog),
                DebugPluginFactory(),
                SysPluginFactory(sdk),
                EthPluginFactory()
            )
            .registerExtensions(
                DecimalExtensionFactory,
                SmtpExtensionFactory,
                HttpExtensionFactory,
                LogExtensionFactory,
                TelegramExtensionFactory
            )
    }
}


class RunnerEnvFactoryDefaultImpl : EnvFactory {
    override fun create(): RunnerEnv {
        return RunnerEnv()
    }
}