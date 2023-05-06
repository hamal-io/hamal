package io.hamal.backend.infra

import io.hamal.backend.infra.adapter.CreateDomainNotificationProcessorPort
import io.hamal.backend.infra.module.orchestrator.handler.TriggerInvokedHandler
import io.hamal.backend.notification.ManualTriggerInvokedNotification
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Configuration
@ComponentScan
@EnableScheduling
@EnableAutoConfiguration(
    exclude = [
        DataSourceAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
        HibernateJpaAutoConfiguration::class,
        JdbcTemplateAutoConfiguration::class,
        SpringApplicationAdminJmxAutoConfiguration::class,
        JmxAutoConfiguration::class
    ]
)
open class BackendConfig {

    @Bean
    open fun domainNotificationConsumer(
        createDomainNotificationConsumerPort: CreateDomainNotificationProcessorPort
    ) = createDomainNotificationConsumerPort
        .register(ManualTriggerInvokedNotification::class, TriggerInvokedHandler())
        .create()

}


@RestController
open class HelloWorld {
    @GetMapping("/v1/hello")
    fun hello(): String = "hello"
}
