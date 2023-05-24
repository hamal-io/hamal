package io.hamal.backend.config.usecase

import io.hamal.backend.cmd.handler.trigger.CreateTriggerRequestHandler
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.TriggerRequestRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RequestTriggerConfig {
    @Bean
    open fun createTriggerRequestHandler(
        eventEmitter: EventEmitter,
        triggerRequestRepository: TriggerRequestRepository
    ) = CreateTriggerRequestHandler(
        eventEmitter,
        triggerRequestRepository
    )

}