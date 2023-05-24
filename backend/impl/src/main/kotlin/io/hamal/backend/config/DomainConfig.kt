package io.hamal.backend.config

import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.event.service.DefaultEventProcessor
import io.hamal.backend.event.service.EventProcessorFactory
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.lib.domain.vo.port.DomainIdGeneratorAdapter
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort
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
    ) = EventEmitter(
        brokerRepository
    )

    @Bean
    open fun createDomainNotificationConsumerPort(
        taskScheduler: ThreadPoolTaskScheduler,
        brokerRepository: BrokerRepository
    ): EventProcessorFactory = DefaultEventProcessor(
        taskScheduler,
        brokerRepository
    )
}
