package io.hamal.backend.config

import io.hamal.backend.component.Async
import io.hamal.backend.component.EventEmitter
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.service.DefaultEventService
import io.hamal.backend.service.EventServiceFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EventConfig {

    @Bean
    open fun eventEmitter(
        logBrokerRepository: LogBrokerRepository
    ) = EventEmitter(
        logBrokerRepository
    )

    @Bean
    open fun eventProcessorFactory(
        async: Async,
        logBrokerRepository: LogBrokerRepository
    ): EventServiceFactory = DefaultEventService(
        async,
        logBrokerRepository
    )
}
