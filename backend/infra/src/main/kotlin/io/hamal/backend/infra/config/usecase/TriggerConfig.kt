package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.infra.usecase.query.trigger.GetTriggerUseCaseHandler
import io.hamal.backend.infra.usecase.query.trigger.ListTriggerUseCaseHandler
import io.hamal.backend.infra.usecase.request.trigger.CreateTriggerRequestHandler
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.TriggerRequestRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RequestTriggerConfig {
    @Bean
    open fun createTriggerRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        triggerRequestRepository: TriggerRequestRepository
    ) = CreateTriggerRequestHandler(
        notifyDomainPort,
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