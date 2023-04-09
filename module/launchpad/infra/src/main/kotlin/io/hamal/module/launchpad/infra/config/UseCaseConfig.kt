package io.hamal.module.launchpad.infra.config

import io.hamal.lib.domain_notification.NotifyDomainPort
import io.hamal.module.launchpad.application.job.CreateJobDefinitionUseCase
import io.hamal.module.launchpad.application.trigger.InvokeManualTriggerUseCase
import io.hamal.module.launchpad.core.job.port.CreateJobDefinitionPort
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
        notifyDomainPort: NotifyDomainPort
    ) = InvokeManualTriggerUseCase.Operation(notifyDomainPort)
}