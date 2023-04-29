package io.hamal.backend.infra

import io.hamal.backend.core.notification.JobDefinitionDomainNotification
import io.hamal.backend.core.notification.Scheduled
import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.infra.adapter.CreateDomainNotificationProcessorPort
import io.hamal.backend.infra.module.job.handler.JobScheduledHandler
import io.hamal.backend.infra.module.job_definition.handler.JobDefinitionCreatedHandler
import io.hamal.backend.infra.module.trigger.handler.TriggerCreatedHandler
import io.hamal.backend.infra.module.trigger.handler.TriggerInvokedHandler
import io.hamal.backend.store.impl.DefaultJobDefinitionStore
import io.hamal.backend.store.impl.DefaultTriggerStore
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Configuration
@ComponentScan
@EnableScheduling
@EnableAutoConfiguration(exclude = [SpringApplicationAdminJmxAutoConfiguration::class, JmxAutoConfiguration::class])
open class BackendConfig {

    @Bean
    open fun domainNotificationConsumer(
        createDomainNotificationConsumerPort: CreateDomainNotificationProcessorPort
    ) = createDomainNotificationConsumerPort
        .register(Scheduled::class, JobScheduledHandler())
        .register(
            JobDefinitionDomainNotification.Created::class,
            JobDefinitionCreatedHandler(DefaultJobDefinitionStore)
        )
        .register(TriggerDomainNotification.Created::class, TriggerCreatedHandler(DefaultTriggerStore))
        .register(TriggerDomainNotification.Invoked::class, TriggerInvokedHandler())
        .create()


}

@RestController
open class HelloWorld {
    @GetMapping("/v1/hello")
    fun hello(): String = "hello"
}