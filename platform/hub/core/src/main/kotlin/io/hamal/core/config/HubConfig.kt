package io.hamal.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

data class HubBasePath(val value: String)

@Configuration
@Profile("!test")
open class HubConfig {

    @Bean
    open fun hubBasePath() = HubBasePath("/tmp/hamal/hub")

}