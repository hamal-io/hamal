package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.usecase.request.adhoc.ExecuteAdhocJobRequestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AdhocJobConfig {
    @Bean
    open fun executeAdhocJobRequestHandler(
        jobRequestRepository: JobDefinitionRepository,
        notifyDomainPort: NotifyDomainPort
    ) = ExecuteAdhocJobRequestHandler(
        notifyDomainPort,
        jobRequestRepository
    )
}