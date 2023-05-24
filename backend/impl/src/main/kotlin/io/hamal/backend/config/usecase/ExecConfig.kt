package io.hamal.backend.config.usecase

import io.hamal.backend.cmd.handler.exec.*
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RequestExecConfig {
    @Bean
    open fun planExecRequestHandler(
        generateDomainIdPort: GenerateDomainIdPort,
        execRequestRepository: ExecRequestRepository,
       eventEmitter: EventEmitter
    ) = PlanExecRequestHandler(
        generateDomainIdPort,
        execRequestRepository,
        eventEmitter
    )

    @Bean
    open fun schedulePlannedExecRequestHandler(
        eventEmitter: EventEmitter,
        execRequestRepository: ExecRequestRepository
    ) = SchedulePlannedExecRequestHandler(
        eventEmitter,
        execRequestRepository
    )

    @Bean
    open fun queueScheduledExecRequestHandler(
        eventEmitter: EventEmitter,
        execRequestRepository: ExecRequestRepository
    ) = QueueScheduledExecRequestHandler(
        eventEmitter,
        execRequestRepository
    )

    @Bean
    open fun dequeueExecRequestHandler(
        eventEmitter: EventEmitter,
        execRequestRepository: ExecRequestRepository
    ) = DequeueExecRequestHandler(
        eventEmitter,
        execRequestRepository
    )

    @Bean
    open fun completeExecRequestHandler(
        eventEmitter: EventEmitter,
        execRequestRepository: ExecRequestRepository
    ) = CompleteStartedExecRequestHandler(
        eventEmitter,
        execRequestRepository
    )
}