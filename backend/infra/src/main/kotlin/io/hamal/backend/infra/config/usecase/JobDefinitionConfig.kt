package io.hamal.backend.infra.config.usecase

import io.hamal.backend.repository.impl.domain.DefaultJobDefinitionRepository
import io.hamal.backend.usecase.request.job_definition.CreateJobDefinitionRequestHandler
import io.hamal.backend.notification.port.NotifyDomainPort
import io.hamal.lib.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JobDefinitionConfig {
    @Bean
    open fun createJobDefinitionRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        generateDomainId: GenerateDomainIdPort,
        jobDefinitionRepository: DefaultJobDefinitionRepository
    ) = CreateJobDefinitionRequestHandler(
        notifyDomainPort,
        generateDomainId,
        jobDefinitionRepository
    )

//    @Bean
//    open fun getJobDefinition() = GetJobDefinitionUseCase.Operation()
}