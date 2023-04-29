package io.hamal.backend.infra.config.usecase

import io.hamal.backend.application.job.CreateJobDefinitionUseCase
import io.hamal.backend.application.job.GetJobDefinitionUseCase
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.lib.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JobDefinitionConfig {
    @Bean
    open fun createJobDefinition(
        notifyDomainPort: NotifyDomainPort,
        generateDomainId: GenerateDomainIdPort
    ) = CreateJobDefinitionUseCase.Operation(notifyDomainPort, generateDomainId)

    @Bean
    open fun getJobDefinition() = GetJobDefinitionUseCase.Operation()
}