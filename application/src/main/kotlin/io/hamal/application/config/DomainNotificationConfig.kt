package io.hamal.application.config

import io.hamal.application.adapter.DomainNotificationAdapter
import io.hamal.lib.domain_notification.CreateDomainNotificationConsumerPort
import io.hamal.lib.domain_notification.NotifyDomainPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class DomainNotificationConfig {

    @Bean(autowireCandidate = false)
    fun domainNotificationAdapter(
        taskScheduler: ThreadPoolTaskScheduler
    ) = DomainNotificationAdapter(taskScheduler)

    @Bean
    fun notifyDomainPort(
        taskScheduler: ThreadPoolTaskScheduler
    ): NotifyDomainPort = domainNotificationAdapter(taskScheduler)

    @Bean
    fun createDomainNotificationConsumerPort(
        taskScheduler: ThreadPoolTaskScheduler
    ): CreateDomainNotificationConsumerPort = domainNotificationAdapter(taskScheduler)
}
