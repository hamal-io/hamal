package io.hamal.application.config

import io.hamal.application.adapter.DomainIdGeneratorAdapter
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IdConfig {
    @Bean
    fun generateIdPort(): GenerateDomainIdPort = DomainIdGeneratorAdapter

}