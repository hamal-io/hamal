package io.hamal.core.config

import io.hamal.core.event.HubEventContainerFactory
import io.hamal.core.event.HubEventEmitter
import io.hamal.core.event.handler.exec.*
import io.hamal.core.event.handler.trigger.TriggerCreatedHandler
import io.hamal.core.service.FixedRateTriggerService
import io.hamal.core.service.MetricService
import io.hamal.core.service.OrchestrationService
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.event.*
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal open class HubEventConfig {
    @Bean
    open fun hubEventEmitter(
        hubEventBrokerRepository: BrokerRepository,
        generateDomainId: GenerateDomainId,
        metricService: MetricService
    ): HubEventEmitter = HubEventEmitter(
        generateDomainId,
        hubEventBrokerRepository,
        metricService
    )

    @Bean
    open fun hubEventContainer(
        execQueryRepository: ExecQueryRepository,
        execCmdRepository: ExecCmdRepository,
        fixedRateTriggerService: FixedRateTriggerService,
        orchestrationService: OrchestrationService,
        eventEmitter: HubEventEmitter
    ) = HubEventContainerFactory()
        .register(TriggerCreatedEvent::class, TriggerCreatedHandler(fixedRateTriggerService))
        .register(ExecPlannedEvent::class, ExecPlannedHandler(orchestrationService))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(execCmdRepository, eventEmitter))
        .register(ExecutionQueuedEvent::class, ExecQueuedHandler())
        .register(ExecutionCompletedEvent::class, ExecCompletedHandler(orchestrationService))
        .register(ExecutionFailedEvent::class, ExecFailedHandler(orchestrationService))
        .create()

}
