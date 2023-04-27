package io.hamal.module.launchpad.infra.config

import io.hamal.lib.domain.vo.port.GenerateDomainIdPort
import io.hamal.lib.domain_notification.NotifyDomainPort
import io.hamal.module.launchpad.application.job.CreateJobDefinitionUseCase
import io.hamal.module.launchpad.application.trigger.InvokeManualTriggerUseCase
import io.hamal.module.launchpad.core.job.CreateJobDefinitionPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UseCaseConfig {

    @Bean
    open fun createJobDefinitionUseCase(
        createPort: CreateJobDefinitionPort,
        notifyDomainPort: NotifyDomainPort
    ) = CreateJobDefinitionUseCase.Operation(createPort, notifyDomainPort)


    @Bean
    open fun invokeManualTrigger(
        notifyDomainPort: NotifyDomainPort,
        generateDomainId: GenerateDomainIdPort
    ) = InvokeManualTriggerUseCase.Operation(notifyDomainPort, generateDomainId)
}