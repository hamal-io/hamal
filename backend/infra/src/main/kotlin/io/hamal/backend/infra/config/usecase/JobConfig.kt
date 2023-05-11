package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.JobRepository
import io.hamal.backend.usecase.request.job.DequeueJobRequestHandler
import io.hamal.backend.usecase.request.job.PlanJobRequestHandler
import io.hamal.backend.usecase.request.job.QueueScheduledJobRequestHandler
import io.hamal.backend.usecase.request.job.SchedulePlannedJobRequestHandler
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JobConfig {
    @Bean
    open fun planJobRequestHandler(
        generateDomainIdPort: GenerateDomainIdPort,
        jobRepository: JobRepository,
        notifyDomainPort: NotifyDomainPort
    ) = PlanJobRequestHandler(
        generateDomainIdPort,
        jobRepository,
        notifyDomainPort
    )

    @Bean
    open fun schedulePlannedJobRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        jobRepository: JobRepository
    ) = SchedulePlannedJobRequestHandler(
        notifyDomainPort,
        jobRepository
    )

    @Bean
    open fun queueScheduledJobRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        jobRepository: JobRepository
    ) = QueueScheduledJobRequestHandler(
        notifyDomainPort,
        jobRepository
    )

    @Bean
    open fun dequeueJobRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        jobRepository: JobRepository
    ) = DequeueJobRequestHandler(
        notifyDomainPort,
        jobRepository
    )
}