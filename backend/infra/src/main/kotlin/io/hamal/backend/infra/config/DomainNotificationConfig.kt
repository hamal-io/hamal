package io.hamal.backend.infra.config

import io.hamal.lib.ddd.port.FlushDomainNotificationPort
import io.hamal.lib.ddd.port.NotifyDomainPort
import io.hamal.backend.infra.adapter.CreateDomainNotificationProcessorPort
import io.hamal.backend.infra.adapter.DefaultDomainNotificationProcessor
import io.hamal.backend.infra.adapter.DomainNotificationAdapter
import io.hamal.backend.repository.api.log.Broker
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.impl.log.DefaultBrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import kotlin.io.path.Path

@Configuration
open class DomainNotificationConfig {

    @Bean
    open fun brokerRepository(): BrokerRepository {
        return DefaultBrokerRepository(Broker(Broker.Id(1), Path("/tmp/hamal")))
    }

    @Bean
    open fun domainNotificationAdapter() = DomainNotificationAdapter(brokerRepository())

    @Bean
    open fun notifyDomainPort(
        brokerRepository: BrokerRepository
    ): NotifyDomainPort = domainNotificationAdapter()

    @Bean
    open fun flushDomainNotificationPort(): FlushDomainNotificationPort = domainNotificationAdapter()

    @Bean
    open fun createDomainNotificationConsumerPort(
        taskScheduler: ThreadPoolTaskScheduler,
        brokerRepository: BrokerRepository
    ): CreateDomainNotificationProcessorPort = DefaultDomainNotificationProcessor(
        taskScheduler,
        brokerRepository
    )
}
