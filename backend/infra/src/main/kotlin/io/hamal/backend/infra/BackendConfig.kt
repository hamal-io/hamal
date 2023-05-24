package io.hamal.backend.infra

import io.hamal.backend.infra.adapter.CreateDomainNotificationProcessorPort
import io.hamal.backend.infra.handler.*
import io.hamal.backend.infra.notification.*
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import io.hamal.lib.domain.ddd.InvokeUseCasePort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.annotation.EnableScheduling

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
open class BackendConfig : ApplicationListener<ContextRefreshedEvent> {

    @Bean
    open fun domainNotificationConsumer(
        createDomainNotificationConsumerPort: CreateDomainNotificationProcessorPort,
        invokeUseCasePort: InvokeUseCasePort
    ) = createDomainNotificationConsumerPort
        .register(AdhocTriggerInvokedNotification::class, AdhocTriggerInvokedHandler(invokeUseCasePort))
        .register(ExecPlannedNotification::class, ExecPlannedHandler(invokeUseCasePort))
        .register(ExecScheduledNotification::class, ExecScheduledHandler(invokeUseCasePort))
        .register(ExecutionQueuedNotification::class, ExecQueuedHandler())
        .register(ExecutionCompletedNotification::class, ExecCompletedHandler())
        .register(ExecutionFailedNotification::class, ExecFailedHandler())
        .create()

    @Autowired
    private lateinit var request: InvokeRequestOneUseCasePort

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
    }

}


