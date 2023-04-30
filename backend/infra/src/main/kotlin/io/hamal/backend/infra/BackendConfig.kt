package io.hamal.backend.infra

import io.hamal.backend.infra.adapter.CreateDomainNotificationProcessorPort
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
//        .register(Scheduled::class, JobScheduledHandler())
//        .register(
//            JobDefinitionDomainNotification.Created::class,
//            JobDefinitionCreatedHandler(DefaultJobDefinitionStore)
//        )
//        .register(TriggerDomainNotification.Created::class, TriggerCreatedHandler(DefaultTriggerStore))
//        .register(TriggerDomainNotification.Invoked::class, TriggerInvokedHandler())
        .create()


}

@RestController
open class HelloWorld {
    @GetMapping("/v1/hello")
    fun hello(): String = "hello"
}