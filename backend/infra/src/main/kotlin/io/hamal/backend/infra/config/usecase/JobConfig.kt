package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobQueryRepository
import io.hamal.backend.repository.api.JobRequestRepository
import io.hamal.backend.usecase.query.job.GetStartedJobUseCaseHandler
import io.hamal.backend.usecase.request.job.*
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RequestJobConfig {
    @Bean
    open fun planJobRequestHandler(
        generateDomainIdPort: GenerateDomainIdPort,
        jobRequestRepository: JobRequestRepository,
        notifyDomainPort: NotifyDomainPort
    ) = PlanJobRequestHandler(
        generateDomainIdPort,
        jobRequestRepository,
        notifyDomainPort
    )

    @Bean
    open fun schedulePlannedJobRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        jobRequestRepository: JobRequestRepository
    ) = SchedulePlannedJobRequestHandler(
        notifyDomainPort,
        jobRequestRepository
    )

    @Bean
    open fun queueScheduledJobRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        jobRequestRepository: JobRequestRepository
    ) = QueueScheduledJobRequestHandler(
        notifyDomainPort,
        jobRequestRepository
    )

    @Bean
    open fun dequeueJobRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        jobRequestRepository: JobRequestRepository
    ) = DequeueJobRequestHandler(
        notifyDomainPort,
        jobRequestRepository
    )

    @Bean
    open fun completeJobRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        jobRequestRepository: JobRequestRepository
    ) = CompleteStartedJobRequestHandler(
        notifyDomainPort,
        jobRequestRepository
    )
}

@Configuration
open class QueryJobConfig {
    @Bean
    open fun getStartedJobUseCaseHandler(
        jobQueryRepository: JobQueryRepository
    ) = GetStartedJobUseCaseHandler(
        jobQueryRepository
    )
}