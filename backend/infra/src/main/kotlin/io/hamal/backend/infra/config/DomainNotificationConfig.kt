package io.hamal.backend.infra.config

import io.hamal.backend.infra.adapter.CreateDomainNotificationConsumerPort
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.infra.adapter.DomainNotificationAdapter
import io.hamal.backend.infra.adapter.DomainNotificationConsumerAdapter
import io.hamal.lib.log.broker.Broker
import io.hamal.lib.log.broker.BrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import kotlin.io.path.Path

@Configuration
open class DomainNotificationConfig {

    @Bean
    open fun brokerRepository(): BrokerRepository {
        return BrokerRepository.open(Broker(Broker.Id(1), Path("/tmp/hamal")))
    }


    @Bean
    open fun notifyDomainPort(
        brokerRepository: BrokerRepository
    ): NotifyDomainPort = DomainNotificationAdapter(brokerRepository)

    @Bean
    open fun createDomainNotificationConsumerPort(
        taskScheduler: ThreadPoolTaskScheduler,
        brokerRepository: BrokerRepository
    ): CreateDomainNotificationConsumerPort = DomainNotificationConsumerAdapter(
        taskScheduler,
        brokerRepository
    )
}
