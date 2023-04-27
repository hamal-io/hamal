package io.hamal.bootstrap.config

import io.hamal.bootstrap.adapter.DomainIdGeneratorAdapter
import io.hamal.lib.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IdConfig {
    @Bean
    fun generateIdPort(): GenerateDomainIdPort = DomainIdGeneratorAdapter

}