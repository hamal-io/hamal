package io.hamal.mono.config

import io.hamal.extension.std.log.LogExtensionFactory
import io.hamal.extension.std.sys.SysExtensionFactory
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.HttpTemplateSupplier
import io.hamal.runner.config.SandboxFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestRunnerConfig {

    @Bean
    fun httpTemplateSupplier(
        @Value("\${server.port}") serverPort: Number
    ): HttpTemplateSupplier = { HttpTemplate("http://localhost:$serverPort") }


    @Bean
    fun sandboxFactory(
        httpTemplateSupplier: HttpTemplateSupplier
    ): SandboxFactory = object : SandboxFactory {
        override fun create(ctx: SandboxContext): Sandbox {
            NativeLoader.load(Resources)
            val sdk = DefaultHamalSdk(httpTemplateSupplier())
            val result = Sandbox(NopSandboxContext())
            result.register(LogExtensionFactory(sdk.execLogService).create())
            result.register(SysExtensionFactory(httpTemplateSupplier).create())
            return result
        }
    }
}