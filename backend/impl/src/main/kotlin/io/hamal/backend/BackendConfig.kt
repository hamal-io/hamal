package io.hamal.backend

import io.hamal.backend.event.*
import io.hamal.backend.event.service.EventProcessorFactory
import io.hamal.backend.event_handler.exec.*
import io.hamal.backend.event_handler.trigger.TriggerInvocationHandler
import io.hamal.backend.service.cmd.ExecCmdService
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
        execCmdService: ExecCmdService
    ) = eventProcessorFactory
        .register(ExecPlannedEvent::class, ExecPlannedHandler(execCmdService))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(execCmdService))
        .register(ExecutionQueuedEvent::class, ExecQueuedHandler())
        .register(ExecutionCompletedEvent::class, ExecCompletedHandler())
        .register(ExecutionFailedEvent::class, ExecFailedHandler())
        .register(TriggerInvocationEvent::class, TriggerInvocationHandler(execCmdService))
        .create()

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
    }

}


