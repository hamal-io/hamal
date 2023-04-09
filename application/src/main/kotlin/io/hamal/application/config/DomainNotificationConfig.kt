package io.hamal.application.config

import io.hamal.application.adapter.DomainNotificationAdapter
import io.hamal.lib.ddd.port.NotifyDomainPort
import io.hamal.lib.domain_notification.JobDefinitionDomainNotification
import io.hamal.lib.domain_notification.JobDomainNotification
import io.hamal.lib.domain_notification.QueueDomainNotification
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainNotificationConfig {
    @Bean
    fun sendJobDefinitionDomainNotificationPort(
    ): NotifyDomainPort<JobDefinitionDomainNotification> =
        DomainNotificationAdapter()

    @Bean
    fun sendJobDomainNotificationPort(
    ): NotifyDomainPort<JobDomainNotification> =
        DomainNotificationAdapter()

    @Bean
    fun sendQueueDomainNotificationPort(
    ): NotifyDomainPort<QueueDomainNotification> =
        DomainNotificationAdapter()
}