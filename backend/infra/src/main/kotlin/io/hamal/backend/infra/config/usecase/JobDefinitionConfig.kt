package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.usecase.request.job_definition.CreateJobDefinitionRequestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JobDefinitionConfig {
    @Bean
    open fun createJobDefinitionRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        jobDefinitionRepository: JobDefinitionRepository
    ) = CreateJobDefinitionRequestHandler(
        notifyDomainPort,
        jobDefinitionRepository
    )

}