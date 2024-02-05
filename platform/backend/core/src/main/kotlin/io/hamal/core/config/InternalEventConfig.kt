package io.hamal.core.config

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.event.InternalEventContainerFactory
import io.hamal.core.event.handler.exec.*
import io.hamal.core.event.handler.trigger.TriggerCreatedHandler
import io.hamal.core.service.FixedRateTriggerService
import io.hamal.core.service.OrchestrationService
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.event.*
import io.hamal.repository.api.log.LogBrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal open class InternalEventConfig {


    @Bean
    open fun internalEventEmitter(
        topicRepository: TopicRepository,
        logBrokerRepository: LogBrokerRepository
    ): InternalEventEmitter = InternalEventEmitter(topicRepository, logBrokerRepository)

    @Bean
    open fun internalEventContainer(
        execQueryRepository: ExecQueryRepository,
        execCmdRepository: ExecCmdRepository,
        fixedRateTriggerService: FixedRateTriggerService,
        orchestrationService: OrchestrationService,
        eventEmitter: InternalEventEmitter
    ) = InternalEventContainerFactory()
        .register(TriggerCreatedEvent::class, TriggerCreatedHandler(fixedRateTriggerService))
        .register(ExecPlannedEvent::class, ExecPlannedHandler(orchestrationService))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(execCmdRepository, eventEmitter))
        .register(ExecQueuedEvent::class, ExecQueuedHandler())
        .register(ExecCompletedEvent::class, ExecCompletedHandler(orchestrationService))
        .register(ExecFailedEvent::class, ExecFailedHandler(orchestrationService))
        .create()

}
