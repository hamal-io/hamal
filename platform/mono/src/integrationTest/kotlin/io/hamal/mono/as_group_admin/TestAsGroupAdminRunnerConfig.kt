package io.hamal.mono.as_group_admin

import io.hamal.extension.std.log.LogExtensionFactory
import io.hamal.extension.std.sys.SysExtensionFactory
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.DefaultHubSdk
import io.hamal.runner.config.SandboxFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class TestAsGroupAdminRunnerConfig {

    @Bean
    fun httpTemplate(
        @Value("\${server.port}") serverPort: Number
    ) = HttpTemplate(
        baseUrl = "http://localhost:$serverPort",
        headerFactory = { }
    )

    @Bean
    fun sandboxFactory(
        httpTemplate: HttpTemplate
    ): SandboxFactory = object : SandboxFactory {
        override fun create(ctx: SandboxContext): Sandbox {
            NativeLoader.load(Resources)
            val sdk = DefaultHubSdk(httpTemplate)
            return Sandbox(NopSandboxContext()).register(
                LogExtensionFactory(sdk.execLog),
                SysExtensionFactory(httpTemplate)
            )
        }
    }
}