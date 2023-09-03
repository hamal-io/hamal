package io.hamal.runner.config

import io.hamal.lib.http.HttpTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class InstanceConfig {
    @Bean
    fun httpTemplate(
        @Value("\${io.hamal.runner.host}") host: String
    ) = HttpTemplate(host)
}