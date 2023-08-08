package io.hamal.backend.instance.config

import io.hamal.backend.instance.component.Async
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.service.DefaultSystemEventService
import io.hamal.backend.instance.service.SystemEventServiceFactory
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EventConfig {
    @Bean
    open fun systemEventEmitter(
        systemEventBrokerRepository: LogBrokerRepository,
        generateDomainId: GenerateDomainId
    ): SystemEventEmitter = SystemEventEmitter(
        generateDomainId,
        systemEventBrokerRepository
    )

    @Bean
    open fun systemEventProcessorFactory(
        async: Async,
        generateDomainId: GenerateDomainId,
        systemEventBrokerRepository: LogBrokerRepository
    ): SystemEventServiceFactory = DefaultSystemEventService(
        async,
        generateDomainId,
        systemEventBrokerRepository
    )
}
