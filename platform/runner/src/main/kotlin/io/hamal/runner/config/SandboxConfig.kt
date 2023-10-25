package io.hamal.runner.config

import io.hamal.script.std.decimal.DecimalScriptFactory
import io.hamal.script.telegram.TelegramScriptFactory
import io.hamal.plugin.net.http.HttpPluginFactory
import io.hamal.plugin.std.debug.DebugPluginFactory
import io.hamal.plugin.std.log.LogPluginFactory
import io.hamal.plugin.web3.evm.EthPluginFactory
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Jar
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.plugin.std.sys.SysPluginFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SandboxConfig {
    @Bean
    open fun sandboxFactory(
        @Value("\${io.hamal.runner.api.host}") apiHost: String,
        @Value("\${io.hamal.runner.bridge.host}") bridgeHost: String
    ): SandboxFactory = RunnerSandboxFactory(
        apiHost, bridgeHost
    )
}

interface SandboxFactory {
    fun create(ctx: SandboxContext): Sandbox
}

class RunnerSandboxFactory(
    private val apiHost: String,
    private val bridgeHost: String

) : SandboxFactory {
    override fun create(ctx: SandboxContext): Sandbox {
        NativeLoader.load(Jar)

        val execToken = ctx[ExecToken::class]
        val template = HttpTemplateImpl(
            baseUrl = apiHost,
            headerFactory = {
                set("x-runner-exec-token", execToken.value)
            }
        )
        val sdk = ApiSdkImpl(template)

        return Sandbox(ctx)
            .register(
                HttpPluginFactory(),
                LogPluginFactory(sdk.execLog),
                DebugPluginFactory(),
                SysPluginFactory(HttpTemplateImpl(apiHost)),
                EthPluginFactory()
            )
            .register(
                DecimalScriptFactory,
                TelegramScriptFactory
            )
    }
}