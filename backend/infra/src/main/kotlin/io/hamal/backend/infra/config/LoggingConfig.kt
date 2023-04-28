package io.hamal.backend.infra.config

import io.hamal.backend.core.port.GetLoggerPort
import io.hamal.backend.infra.adapter.BackendLoggingFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class LoggingConfig {
    @Bean
    open fun getLogger(): GetLoggerPort = BackendLoggingFactory
}