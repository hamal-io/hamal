package io.hamal.application.config

import io.hamal.application.adapter.DomainNotificationAdapter
import io.hamal.application.adapter.DomainNotificationConsumerAdapter
import io.hamal.lib.domain_notification.CreateDomainNotificationConsumerPort
import io.hamal.lib.domain_notification.NotifyDomainPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class DomainNotificationConfig {

    @Bean
    fun notifyDomainPort(
        taskScheduler: ThreadPoolTaskScheduler
    ): NotifyDomainPort = DomainNotificationAdapter()

    @Bean
    fun createDomainNotificationConsumerPort(
        taskScheduler: ThreadPoolTaskScheduler
    ): CreateDomainNotificationConsumerPort = DomainNotificationConsumerAdapter(
        taskScheduler
    )
}
