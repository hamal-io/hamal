package io.hamal.agent.config

import io.hamal.agent.extension.std.sys.SysExtensionFactory
import io.hamal.lib.domain.Once
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.FixedPathLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
open class SandboxConfig {
    companion object {
        @JvmStatic
        val once = Once.default<Unit>()
    }

    @Bean
    open fun sandboxFactory(): SandboxFactory = object : SandboxFactory {
        override fun create(): Sandbox {
            FixedPathLoader.load()
            val template = HttpTemplate("http://localhost:8084")
            return Sandbox().also {
                it.register(SysExtensionFactory { template }.create())
            }
        }
    }
}