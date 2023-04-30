package io.hamal.bootstrap.config

import io.hamal.lib.vo.port.DomainIdGeneratorAdapter
import io.hamal.lib.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IdConfig {
    @Bean
    fun generateIdPort(): GenerateDomainIdPort = DomainIdGeneratorAdapter

}