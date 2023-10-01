package io.hamal.runner.config

import io.hamal.lib.http.HttpTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RunnerBridgeConfig {
    @Bean
    fun httpTemplate(
        @Value("\${io.hamal.runner.bridge.host}") host: String
    ) = HttpTemplate(
        baseUrl = host,
        headerFactory = {
            set("x-runner-token", "i_am_runner_let_me_in")
        }
    )
}