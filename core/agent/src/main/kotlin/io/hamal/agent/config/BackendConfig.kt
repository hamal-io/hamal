package io.hamal.agent.config

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.HttpTemplateSupplier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
open class BackendConfig {
    @Bean
    open fun httpTemplate(): HttpTemplateSupplier = { HttpTemplate("http://localhost:8008") }
}