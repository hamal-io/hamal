package io.hamal.backend.infra.config

import io.hamal.backend.infra.adapter.CreateDomainNotificationProcessorPort
import io.hamal.backend.infra.adapter.DefaultDomainNotificationProcessor
import io.hamal.backend.infra.adapter.DomainNotificationAdapter
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.lib.core.vo.port.DomainIdGeneratorAdapter
import io.hamal.lib.core.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
open class DomainConfig {

    @Bean
    open fun generateDomainIdPort(): GenerateDomainIdPort = DomainIdGeneratorAdapter

    @Bean
    open fun domainNotificationAdapter(
        brokerRepository: BrokerRepository
    ) = DomainNotificationAdapter(
        brokerRepository
    )

    @Bean
    open fun createDomainNotificationConsumerPort(
        taskScheduler: ThreadPoolTaskScheduler,
        brokerRepository: BrokerRepository
    ): CreateDomainNotificationProcessorPort = DefaultDomainNotificationProcessor(
        taskScheduler,
        brokerRepository
    )
}
