package io.hamal.backend.infra.config.usecase

import io.hamal.backend.application.trigger.InvokeManualTriggerUseCase
import io.hamal.lib.vo.port.GenerateDomainIdPort
import io.hamal.backend.core.port.notification.NotifyDomainPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TriggerConfig {

    @Bean
    open fun invokeManualTrigger(
        notifyDomainPort: NotifyDomainPort,
        generateDomainId: GenerateDomainIdPort
    ) = InvokeManualTriggerUseCase.Operation(notifyDomainPort, generateDomainId)

}