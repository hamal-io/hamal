package io.hamal.backend.config.usecase

import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.TriggerRequestRepository
import io.hamal.backend.usecase.handler.trigger.GetTriggerUseCaseHandler
import io.hamal.backend.usecase.handler.trigger.ListTriggerUseCaseHandler
import io.hamal.backend.usecase.handler.trigger.CreateTriggerRequestHandler
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

@Configuration
open class QueryTriggerConfig {

    @Bean
    open fun listTriggerUseCaseHandler(
        triggerQueryRepository: TriggerQueryRepository
    ) = ListTriggerUseCaseHandler(
        triggerQueryRepository
    )

    @Bean
    open fun getTriggerUseCaseHandler(
        triggerQueryRepository: TriggerQueryRepository
    ) = GetTriggerUseCaseHandler(
        triggerQueryRepository
    )

}