package io.hamal.runner.config

import io.hamal.extension.std.log.LogExtensionFactory
import io.hamal.extension.std.sys.SysExtensionFactory
import io.hamal.extension.web3.eth.EthExtensionFactory
import io.hamal.extension.web3.hml.HmlExtensionFactory
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.DefaultSandboxContext
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.sdk.domain.DequeueExecsResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
open class SandboxConfig {
    @Bean
    open fun sandboxFactory(): SandboxFactory = RunnerSandboxFactory()
}

interface SandboxFactory {
    fun create(exec: DequeueExecsResponse.Exec): Sandbox
}

class RunnerSandboxFactory : SandboxFactory {
    override fun create(exec: DequeueExecsResponse.Exec): Sandbox {
        NativeLoader.load(NativeLoader.Preference.Jar)
        val template = HttpTemplate("http://localhost:8008")

        val ctx = DefaultSandboxContext()
        ctx[ExecId::class] = exec.id


        return Sandbox(ctx).also {
            it.register(LogExtensionFactory { template }.create())
            it.register(SysExtensionFactory { template }.create())
            it.register(EthExtensionFactory().create())
            it.register(HmlExtensionFactory().create())
        }
    }

}