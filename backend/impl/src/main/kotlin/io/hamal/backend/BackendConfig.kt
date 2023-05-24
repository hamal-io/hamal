package io.hamal.backend

import io.hamal.backend.event.*
import io.hamal.backend.event.handler.*
import io.hamal.backend.event.handler.adhoc.AdhocTriggerInvokedHandler
import io.hamal.backend.event.handler.exec.*
import io.hamal.backend.event.service.EventProcessorFactory
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
        eventProcessorFactory: EventProcessorFactory,
        invokeUseCasePort: InvokeUseCasePort
    ) = eventProcessorFactory
        .register(AdhocTriggerInvokedEvent::class, AdhocTriggerInvokedHandler(invokeUseCasePort))
        .register(ExecPlannedNotification::class, ExecPlannedHandler(invokeUseCasePort))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(invokeUseCasePort))
        .register(ExecutionQueuedEvent::class, ExecQueuedHandler())
        .register(ExecutionCompletedEvent::class, ExecCompletedHandler())
        .register(ExecutionFailedEvent::class, ExecFailedHandler())
        .create()

    @Autowired
    private lateinit var request: InvokeRequestOneUseCasePort

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
    }

}


