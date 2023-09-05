package io.hamal.api.config

import io.hamal.api.event.HubEventContainerFactory
import io.hamal.api.event.HubEventEmitter
import io.hamal.repository.api.event.*
import io.hamal.api.event.handler.exec.*
import io.hamal.api.event.handler.trigger.TriggerCreatedHandler
import io.hamal.api.service.FixedRateTriggerService
import io.hamal.api.service.MetricService
import io.hamal.api.service.OrchestrationService
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
