package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.FuncRepository
import io.hamal.backend.usecase.request.trigger.ManualTriggerInvocationRequestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TriggerConfig {

    @Bean
    open fun invokeManualTrigger(
        notifyDomainPort: NotifyDomainPort,
        funcRepository: FuncRepository
    ) = ManualTriggerInvocationRequestHandler(
        notifyDomainPort,
        funcRepository
    )

//    @Bean
//    open fun getTrigger() = GetTriggerUseCase.Operation()

}