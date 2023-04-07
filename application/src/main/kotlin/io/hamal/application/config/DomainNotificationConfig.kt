package io.hamal.application.config

import io.hamal.application.adapter.DomainNotificationAdapter
import io.hamal.lib.ddd.port.NotifyDomainPort
import io.hamal.lib.domain_notification.JobDefinitionDomainNotification
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DomainNotificationConfig {
    @Bean
    open fun sendDomainNotificationPort(): NotifyDomainPort<JobDefinitionDomainNotification> =
        DomainNotificationAdapter()
}