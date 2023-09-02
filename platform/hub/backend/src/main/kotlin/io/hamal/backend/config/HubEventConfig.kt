package io.hamal.backend.config

import io.hamal.backend.event.HubEventContainerFactory
import io.hamal.backend.event.HubEventEmitter
import io.hamal.repository.api.event.*
import io.hamal.backend.event.handler.exec.*
import io.hamal.backend.event.handler.trigger.TriggerCreatedHandler
import io.hamal.backend.service.FixedRateTriggerService
import io.hamal.backend.service.MetricService
import io.hamal.backend.service.OrchestrationService
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class HubEventConfig {
    @Bean
    open fun hubEventEmitter(
        hubEventBrokerRepository: BrokerRepository,
        generateDomainId: GenerateDomainId
    ): HubEventEmitter = HubEventEmitter(
        generateDomainId,
        hubEventBrokerRepository
    )

    @Bean
    open fun hubEventContainer(
        execQueryRepository: ExecQueryRepository,
        execCmdRepository: ExecCmdRepository,
        fixedRateTriggerService: FixedRateTriggerService,
        orchestrationService: OrchestrationService,
        metricService: MetricService,
        eventEmitter: HubEventEmitter
    ) = HubEventContainerFactory()
        .register(TriggerCreatedEvent::class, TriggerCreatedHandler(fixedRateTriggerService))
        .register(ExecPlannedEvent::class, ExecPlannedHandler(orchestrationService))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(execCmdRepository, eventEmitter))
        .register(ExecutionQueuedEvent::class, ExecQueuedHandler())
        .register(ExecutionCompletedEvent::class, ExecCompletedHandler(orchestrationService, metricService))
        .register(ExecutionFailedEvent::class, ExecFailedHandler(orchestrationService))
        .create()

}
