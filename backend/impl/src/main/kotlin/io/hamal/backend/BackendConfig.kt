package io.hamal.backend

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.event.*
import io.hamal.backend.event_handler.exec.*
import io.hamal.backend.event_handler.trigger.TriggerCreatedHandler
import io.hamal.backend.service.EventServiceFactory
import io.hamal.backend.service.OrchestrationService
import io.hamal.backend.service.FixedRateTriggerService
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.ReqCmdService
import io.hamal.backend.service.cmd.StateCmdService
import io.hamal.backend.service.query.ExecQueryService
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
        eventServiceFactory: EventServiceFactory,
        execCmdService: ExecCmdService,
        execQueryService: ExecQueryService,
        stateCmdService: StateCmdService,
        fixedRateTriggerService: FixedRateTriggerService,
        orchestrationService: OrchestrationService,
        reqCmdService: ReqCmdService,
        eventEmitter: EventEmitter
    ) = eventServiceFactory
        .register(TriggerCreatedEvent::class, TriggerCreatedHandler(fixedRateTriggerService))

        .register(ExecPlannedEvent::class, ExecPlannedHandler(orchestrationService))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(execCmdService))
        .register(ExecutionQueuedEvent::class, ExecQueuedHandler())
        .register(ExecutionCompletedEvent::class, ExecCompletedHandler(orchestrationService))
        .register(ExecutionFailedEvent::class, ExecFailedHandler(orchestrationService))

        .create()

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
    }

}


