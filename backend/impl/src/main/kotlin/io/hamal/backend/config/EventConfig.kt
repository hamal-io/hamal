package io.hamal.backend.config

import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.event.service.DefaultEventProcessor
import io.hamal.backend.event.service.EventProcessorFactory
import io.hamal.backend.repository.api.log.LogBrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

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
        taskScheduler: ThreadPoolTaskScheduler,
        logBrokerRepository: LogBrokerRepository
    ): EventProcessorFactory = DefaultEventProcessor(
        taskScheduler,
        logBrokerRepository
    )
}
