package io.hamal.application.config

import io.hamal.application.adapter.DomainNotificationAdapter
import io.hamal.lib.domain_notification.HandleDomainNotificationPort
import io.hamal.lib.domain_notification.NotifyDomainPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainNotificationConfig {

    @Bean(autowireCandidate = false)
    fun domainNotificationAdapter() = DomainNotificationAdapter()

    @Bean
    fun notifyDomainPort(): NotifyDomainPort = domainNotificationAdapter()

    @Bean
    fun handleDomainNotificationPort(): HandleDomainNotificationPort = domainNotificationAdapter()
}
