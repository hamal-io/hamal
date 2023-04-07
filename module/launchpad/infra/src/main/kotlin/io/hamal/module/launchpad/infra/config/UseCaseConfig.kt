package io.hamal.module.launchpad.infra.config

import io.hamal.lib.ddd.port.NotifyDomainPort
import io.hamal.lib.domain_notification.JobDefinitionDomainNotification
import io.hamal.module.launchpad.application.job.CreateJobDefinitionUseCase
import io.hamal.module.launchpad.core.job.port.CreateJobDefinitionPort
import io.hamal.module.launchpad.infra.adapter.JobDefinitionAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UseCaseConfig {

    @Bean
    open fun createJobDefinitionUseCase(
        createPort: CreateJobDefinitionPort,
        notifyDomainPort: NotifyDomainPort<JobDefinitionDomainNotification>
    ) = CreateJobDefinitionUseCase.Operation(createPort, notifyDomainPort)
}