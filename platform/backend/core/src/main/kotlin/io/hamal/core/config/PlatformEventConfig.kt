package io.hamal.core.config

import io.hamal.core.event.PlatformEventContainerFactory
import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.event.handler.exec.*
import io.hamal.core.event.handler.trigger.TriggerCreatedHandler
import io.hamal.core.service.FixedRateTriggerService
import io.hamal.core.service.OrchestrationService
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.event.*
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal open class PlatformEventConfig {
    @Bean
    open fun platformEventEmitter(
        platformEventBrokerRepository: BrokerRepository,
        generateDomainId: GenerateDomainId
    ): PlatformEventEmitter = PlatformEventEmitter(generateDomainId, platformEventBrokerRepository)

    @Bean
    open fun platformEventContainer(
        execQueryRepository: ExecQueryRepository,
        execCmdRepository: ExecCmdRepository,
        fixedRateTriggerService: FixedRateTriggerService,
        orchestrationService: OrchestrationService,
        eventEmitter: PlatformEventEmitter
    ) = PlatformEventContainerFactory()
        .register(TriggerCreatedEvent::class, TriggerCreatedHandler(fixedRateTriggerService))
        .register(ExecPlannedEvent::class, ExecPlannedHandler(orchestrationService))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(execCmdRepository, eventEmitter))
        .register(ExecutionQueuedEvent::class, ExecQueuedHandler())
        .register(ExecutionCompletedEvent::class, ExecCompletedHandler(orchestrationService))
        .register(ExecutionFailedEvent::class, ExecFailedHandler(orchestrationService))
        .create()

}
