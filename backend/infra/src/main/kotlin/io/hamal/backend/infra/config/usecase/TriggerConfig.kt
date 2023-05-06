package io.hamal.backend.infra.config.usecase

import io.hamal.backend.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.usecase.request.trigger.ManualTriggerInvocationRequestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TriggerConfig {

    @Bean
    open fun invokeManualTrigger(
        notifyDomainPort: NotifyDomainPort,
        jobDefinitionRepository: JobDefinitionRepository
    ) = ManualTriggerInvocationRequestHandler(
        notifyDomainPort,
        jobDefinitionRepository
    )

//    @Bean
//    open fun getTrigger() = GetTriggerUseCase.Operation()

}