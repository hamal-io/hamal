package io.module.hamal.queue.infra

import io.hamal.lib.domain_notification.CreateDomainNotificationConsumerPort
import io.hamal.lib.domain_notification.notification.Scheduled
import io.module.hamal.queue.infra.handler.JobScheduledHandler
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling


@Configuration
@ComponentScan
@EnableScheduling
@EnableAutoConfiguration(exclude = [SpringApplicationAdminJmxAutoConfiguration::class, JmxAutoConfiguration::class])
open class QueueModuleConfig {

    @Bean
    open fun domainNotificationConsumer(
        createDomainNotificationConsumerPort: CreateDomainNotificationConsumerPort
    ) = createDomainNotificationConsumerPort
        .register(Scheduled::class, JobScheduledHandler())
        .create()

}
