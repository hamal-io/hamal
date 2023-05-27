package io.hamal.backend

import io.hamal.backend.event.*
import io.hamal.backend.event.service.EventProcessorFactory
import io.hamal.backend.event_handler.exec.*
import io.hamal.backend.event_handler.invocation.AdhocInvocationHandler
import io.hamal.backend.event_handler.invocation.EventInvocationHandler
import io.hamal.backend.event_handler.invocation.FixedDelayInvocationHandler
import io.hamal.backend.event_handler.invocation.OneshotInvocationHandler
import io.hamal.backend.event_handler.trigger.TriggerCreatedHandler
import io.hamal.backend.service.TriggerInvocationService
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
    open fun backendEventConsumer(
        eventProcessorFactory: EventProcessorFactory,
        execCmdService: ExecCmdService,
        triggerInvocationService: TriggerInvocationService
    ) = eventProcessorFactory

        .register(TriggerCreatedEvent::class, TriggerCreatedHandler(triggerInvocationService))

        .register(ExecPlannedEvent::class, ExecPlannedHandler(execCmdService))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(execCmdService))
        .register(ExecutionQueuedEvent::class, ExecQueuedHandler())
        .register(ExecutionCompletedEvent::class, ExecCompletedHandler())
        .register(ExecutionFailedEvent::class, ExecFailedHandler())

        .register(AdhocInvocationEvent::class, AdhocInvocationHandler(execCmdService))
        .register(OneshotInvocationEvent::class, OneshotInvocationHandler(execCmdService))
        .register(EventInvocationEvent::class, EventInvocationHandler(execCmdService))
        .register(FixedDelayInvocationEvent::class, FixedDelayInvocationHandler(execCmdService))

        .create()

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
    }

}


