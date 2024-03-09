package io.hamal.core.config

import io.hamal.core.event.InternalEventContainerFactory
import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.event.handler.exec.*
import io.hamal.core.event.handler.trigger.TriggerCreatedHandler
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
        triggerCreatedHandler: TriggerCreatedHandler,
        execPlannedHandler: ExecPlannedHandler,
        execScheduledHandler: ExecScheduledHandler,
        execQueuedHandler: ExecQueuedHandler,
        execCompletedHandler: ExecCompletedHandler,
        execFailedHandler: ExecFailedHandler
    ) = InternalEventContainerFactory()
        .register(TriggerCreatedEvent::class, triggerCreatedHandler)
        .register(ExecPlannedEvent::class, execPlannedHandler)
        .register(ExecScheduledEvent::class, execScheduledHandler)
        .register(ExecQueuedEvent::class, execQueuedHandler)
        .register(ExecCompletedEvent::class, execCompletedHandler)
        .register(ExecFailedEvent::class, execFailedHandler)
        .create()

}
