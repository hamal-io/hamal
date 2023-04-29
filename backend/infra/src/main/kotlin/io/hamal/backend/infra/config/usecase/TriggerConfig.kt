package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.request.trigger.ManualTriggerInvocation
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TriggerConfig {

    @Bean
    open fun invokeManualTrigger(

        invokeUseCasePort: InvokeUseCasePort,
        notifyDomainPort: NotifyDomainPort,
        generateDomainId: GenerateDomainIdPort
    ) = ManualTriggerInvocation.Operation(invokeUseCasePort, notifyDomainPort, generateDomainId)

//    @Bean
//    open fun getTrigger() = GetTriggerUseCase.Operation()

}