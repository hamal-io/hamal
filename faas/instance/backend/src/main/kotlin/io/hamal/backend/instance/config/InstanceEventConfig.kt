package io.hamal.backend.instance.config

import io.hamal.backend.instance.event.InstanceEventContainerFactory
import io.hamal.backend.instance.event.InstanceEventEmitter
import io.hamal.backend.instance.event.events.*
import io.hamal.backend.instance.event.handler.exec.*
import io.hamal.backend.instance.event.handler.trigger.TriggerCreatedHandler
import io.hamal.backend.instance.service.FixedRateTriggerService
import io.hamal.backend.instance.service.MetricService
import io.hamal.backend.instance.service.OrchestrationService
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class InstanceEventConfig {
    @Bean
    open fun instanceEventEmitter(
        instanceEventBrokerRepository: BrokerRepository,
        generateDomainId: GenerateDomainId
    ): InstanceEventEmitter = InstanceEventEmitter(
        generateDomainId,
        instanceEventBrokerRepository
    )

    @Bean
    open fun instanceEventContainer(
        execQueryRepository: ExecQueryRepository,
        execCmdRepository: ExecCmdRepository,
        fixedRateTriggerService: FixedRateTriggerService,
        orchestrationService: OrchestrationService,
        metricService: MetricService,
        eventEmitter: InstanceEventEmitter
    ) = InstanceEventContainerFactory()
        .register(TriggerCreatedEvent::class, TriggerCreatedHandler(fixedRateTriggerService))
        .register(ExecPlannedEvent::class, ExecPlannedHandler(orchestrationService))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(execCmdRepository, eventEmitter))
        .register(ExecutionQueuedEvent::class, ExecQueuedHandler())
        .register(ExecutionCompletedEvent::class, ExecCompletedHandler(orchestrationService, metricService))
        .register(ExecutionFailedEvent::class, ExecFailedHandler(orchestrationService))
        .create()

}
