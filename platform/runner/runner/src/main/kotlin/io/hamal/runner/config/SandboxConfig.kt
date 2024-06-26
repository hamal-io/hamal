package io.hamal.runner.config

import io.hamal.extension.net.http.ExtensionNetHttpFactory
import io.hamal.extension.net.smtp.ExtensionNetSmtpFactory
import io.hamal.extension.social.telegram.ExtensionSocialTelegramFactory
import io.hamal.extension.std.algo.ExtensionStdAlgoFactory
import io.hamal.extension.std.debug.ExtensionStdDebugFactory
import io.hamal.extension.std.decimal.ExtensionStdDecimalFactory
import io.hamal.extension.std.log.ExtensionStdLogFactory
import io.hamal.extension.std.memoize.ExtensionStdMemoizeFactory
import io.hamal.extension.std.once.ExtensionStdOnceFactory
import io.hamal.extension.std.sys.ExtensionStdSysFactory
import io.hamal.extension.std.table.ExtensionStdTableFactory
import io.hamal.extension.std.`throw`.ExtensionStdThrowFactory
import io.hamal.extension.web3.arbitrum.ExtensionWeb3ArbitrumFactory
import io.hamal.extension.web3.eth.ExtensionWeb3EthFactory
import io.hamal.extension.web3.nyanbot.ExtensionWeb3NyanbotFactory
import io.hamal.lib.domain.vo.AuthToken.Companion.AuthToken
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Jar
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.nodes.compiler.node.defaultNodeCompilerRegistry
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.plugin.net.http.PluginHttpFactory
import io.hamal.plugin.net.smtp.PluginSmtpFactory
import io.hamal.plugin.std.debug.PluginDebugFactory
import io.hamal.plugin.std.log.PluginLogFactory
import io.hamal.plugin.std.sys.PluginSysFactory
import io.hamal.plugin.web3.evm.evm.PluginWeb3EvmFactory
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
            token = AuthToken("RunnerToken"),
            execToken = ctx[ExecToken::class]
        )

        return Sandbox(ctx)
            .registerExtensions(
                ExtensionStdTableFactory,
                ExtensionStdThrowFactory,
                ExtensionStdMemoizeFactory,
                ExtensionStdOnceFactory,
                ExtensionStdDecimalFactory
            )
            .registerPlugins(
                PluginSmtpFactory(),
                PluginHttpFactory(),
                PluginLogFactory(sdk.execLog),
                PluginDebugFactory(),
                PluginSysFactory(sdk),
                PluginWeb3EvmFactory()
            )
            .registerExtensions(
                ExtensionStdAlgoFactory,
                ExtensionStdLogFactory,
                ExtensionStdSysFactory,

                ExtensionNetSmtpFactory,
                ExtensionNetHttpFactory,
                ExtensionStdDebugFactory,

                ExtensionSocialTelegramFactory,

                ExtensionWeb3ArbitrumFactory,
                ExtensionWeb3EthFactory,
                ExtensionWeb3NyanbotFactory
            ).also { sandbox ->
                sandbox.generatorNodeCompilerRegistry.register(defaultNodeCompilerRegistry)
            }
    }
}


class RunnerEnvFactoryDefaultImpl : EnvFactory {
    override fun create(): RunnerEnv {
        return RunnerEnv()
    }
}