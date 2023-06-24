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
    open fun eventEmitter(
        logBrokerRepository: LogBrokerRepository<*>,
        generateDomainId: GenerateDomainId
    ): SystemEventEmitter<*> = SystemEventEmitter(
        generateDomainId,
        logBrokerRepository
    )

    @Bean
    open fun eventProcessorFactory(
        async: Async,
        generateDomainId: GenerateDomainId,
        logBrokerRepository: LogBrokerRepository<*>
    ): SystemEventServiceFactory = DefaultSystemEventService(
        async,
        generateDomainId,
        logBrokerRepository
    )
}
