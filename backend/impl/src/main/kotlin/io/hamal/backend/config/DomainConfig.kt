package io.hamal.backend.config

import io.hamal.lib.domain.vo.port.DefaultDomainIdGenerator
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DomainConfig {
    @Bean
    open fun generateDomainId(): GenerateDomainId = DefaultDomainIdGenerator

}
