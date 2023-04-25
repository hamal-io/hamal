package io.hamal.application.config

import io.hamal.application.adapter.DomainNotificationAdapter
import io.hamal.application.adapter.DomainNotificationConsumerAdapter
import io.hamal.lib.domain_notification.CreateDomainNotificationConsumerPort
import io.hamal.lib.domain_notification.NotifyDomainPort
import io.hamal.lib.log.broker.Broker
import io.hamal.lib.log.broker.BrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import kotlin.io.path.Path

@Configuration
class DomainNotificationConfig {

    @Bean
    fun brokerRepository(): BrokerRepository {
        return BrokerRepository.open(Broker(Broker.Id(1), Path("/tmp/hamal")))
    }


    @Bean
    fun notifyDomainPort(
        brokerRepository: BrokerRepository
    ): NotifyDomainPort = DomainNotificationAdapter(brokerRepository)

    @Bean
    fun createDomainNotificationConsumerPort(
        taskScheduler: ThreadPoolTaskScheduler,
        brokerRepository: BrokerRepository
    ): CreateDomainNotificationConsumerPort = DomainNotificationConsumerAdapter(
        taskScheduler,
        brokerRepository
    )
}
