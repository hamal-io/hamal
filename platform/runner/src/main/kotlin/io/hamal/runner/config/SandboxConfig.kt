package io.hamal.runner.config

import io.hamal.extension.safe.std.decimal.DecimalSafeFactory
import io.hamal.extension.safe.telegram.TelegramSafeFactory
import io.hamal.extension.unsafe.net.http.HttpExtensionFactory
import io.hamal.extension.unsafe.std.debug.DebugExtensionFactory
import io.hamal.extension.unsafe.std.log.LogExtensionFactory
import io.hamal.extension.unsafe.std.sys.SysExtensionFactory
import io.hamal.extension.unsafe.web3.evm.EthExtensionFactory
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Jar
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.ApiSdkImpl
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
                HttpExtensionFactory(),
                LogExtensionFactory(sdk.execLog),
                DebugExtensionFactory(),
                SysExtensionFactory(HttpTemplateImpl(apiHost)),
                EthExtensionFactory()
            )
            .register(
                DecimalSafeFactory,
                TelegramSafeFactory
            )
    }
}