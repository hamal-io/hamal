package io.module.hamal.queue.infra.config

import io.hamal.lib.ddd.port.NotifyDomainPort
import io.hamal.lib.domain_notification.QueueDomainNotification
import io.hamal.module.queue.application.DequeueJobUseCase
import io.hamal.module.queue.application.EnqueueJobUseCase
import io.module.hamal.queue.infra.adapter.QueueAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UseCaseConfig {
    @Bean
    open fun enqueueJob(
        queueAdapter: QueueAdapter,
        notifyDomainPort: NotifyDomainPort<QueueDomainNotification>
    ) = EnqueueJobUseCase.Operation(queueAdapter, notifyDomainPort)

    @Bean
    open fun dequeueJob(
        queueAdapter: QueueAdapter
    ) = DequeueJobUseCase.Operation(queueAdapter)
}