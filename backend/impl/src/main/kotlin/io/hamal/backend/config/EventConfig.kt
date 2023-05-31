package io.hamal.backend.config

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.service.DefaultEventProcessor
import io.hamal.backend.service.EventProcessorFactory
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
