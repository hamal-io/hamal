package io.hamal.core.config

import io.hamal.lib.common.Partition
import io.hamal.lib.domain.DomainIdGeneratorImpl
import io.hamal.lib.domain.GenerateDomainId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DomainConfig {
    @Bean
    open fun generateDomainId(): GenerateDomainId = DomainIdGeneratorImpl(Partition(1))

}
