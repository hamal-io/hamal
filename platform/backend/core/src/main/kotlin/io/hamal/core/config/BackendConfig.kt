package io.hamal.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

data class BackendBasePath(val value: String)

@Configuration
@Profile("!test")
open class BackendConfig {

    @Bean
    open fun backendBasePath() = BackendBasePath("/opt/hamal/backend")

}