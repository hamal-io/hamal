package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.backend.usecase.query.exec.GetExecUseCaseHandler
import io.hamal.backend.usecase.query.exec.GetStartedExecUseCaseHandler
import io.hamal.backend.usecase.query.exec.ListExecsUseCaseHandler
import io.hamal.backend.usecase.request.exec.*
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RequestExecConfig {
    @Bean
    open fun planExecRequestHandler(
        generateDomainIdPort: GenerateDomainIdPort,
        execRequestRepository: ExecRequestRepository,
        notifyDomainPort: NotifyDomainPort
    ) = PlanExecRequestHandler(
        generateDomainIdPort,
        execRequestRepository,
        notifyDomainPort
    )

    @Bean
    open fun schedulePlannedExecRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        execRequestRepository: ExecRequestRepository
    ) = SchedulePlannedExecRequestHandler(
        notifyDomainPort,
        execRequestRepository
    )

    @Bean
    open fun queueScheduledExecRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        execRequestRepository: ExecRequestRepository
    ) = QueueScheduledExecRequestHandler(
        notifyDomainPort,
        execRequestRepository
    )

    @Bean
    open fun dequeueExecRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        execRequestRepository: ExecRequestRepository
    ) = DequeueExecRequestHandler(
        notifyDomainPort,
        execRequestRepository
    )

    @Bean
    open fun completeExecRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        execRequestRepository: ExecRequestRepository
    ) = CompleteStartedExecRequestHandler(
        notifyDomainPort,
        execRequestRepository
    )
}

@Configuration
open class QueryExecConfig {
    @Bean
    open fun getStartedExecUseCaseHandler(
        execQueryRepository: ExecQueryRepository
    ) = GetStartedExecUseCaseHandler(
        execQueryRepository
    )

    @Bean
    open fun listExecsUseCaseHandler(
        execQueryRepository: ExecQueryRepository
    ) = ListExecsUseCaseHandler(
        execQueryRepository
    )

    @Bean
    open fun getExecUseCaseHandler(
        execQueryRepository: ExecQueryRepository
    ) = GetExecUseCaseHandler(
        execQueryRepository
    )

}