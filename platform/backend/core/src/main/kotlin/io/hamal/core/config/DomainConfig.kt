package io.hamal.core.config

import io.hamal.lib.common.Partition
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.IdGeneratorImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DomainConfig {
    @Bean
    open fun generateDomainId(): GenerateDomainId = IdGeneratorImpl(Partition(1))

}
