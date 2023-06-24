package io.hamal.backend.config

import io.hamal.backend.component.Async
import io.hamal.backend.component.SystemEventEmitter
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.service.DefaultSystemEventService
import io.hamal.backend.service.SystemEventServiceFactory
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EventConfig {
    @Bean
    open fun systemEventEmitter(
        systemEventBrokerRepository: LogBrokerRepository<*>,
        generateDomainId: GenerateDomainId
    ): SystemEventEmitter<*> = SystemEventEmitter(
        generateDomainId,
        systemEventBrokerRepository
    )

    @Bean
    open fun systemEventProcessorFactory(
        async: Async,
        generateDomainId: GenerateDomainId,
        systemEventBrokerRepository: LogBrokerRepository<*>
    ): SystemEventServiceFactory = DefaultSystemEventService(
        async,
        generateDomainId,
        systemEventBrokerRepository
    )
}
