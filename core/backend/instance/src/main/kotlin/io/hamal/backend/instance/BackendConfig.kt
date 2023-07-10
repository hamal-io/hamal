package io.hamal.backend.instance

import io.hamal.backend.instance.event.*
import io.hamal.backend.instance.event.handler.exec.*
import io.hamal.backend.instance.event.handler.trigger.TriggerCreatedHandler
import io.hamal.backend.instance.service.FixedRateTriggerService
import io.hamal.backend.instance.service.OrchestrationService
import io.hamal.backend.instance.service.SystemEventServiceFactory
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecQueryRepository
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
        eventServiceFactory: SystemEventServiceFactory,
        execQueryRepository: ExecQueryRepository,
        execCmdRepository: ExecCmdRepository,
        fixedRateTriggerService: FixedRateTriggerService,
        orchestrationService: OrchestrationService,
        eventEmitter: SystemEventEmitter<*>
    ) = eventServiceFactory
        .register(TriggerCreatedEvent::class, TriggerCreatedHandler(fixedRateTriggerService))

        .register(ExecPlannedEvent::class, ExecPlannedHandler(orchestrationService))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(execCmdRepository, eventEmitter))
        .register(ExecutionQueuedEvent::class, ExecQueuedHandler())
        .register(ExecutionCompletedEvent::class, ExecCompletedHandler(orchestrationService))
        .register(ExecutionFailedEvent::class, ExecFailedHandler(orchestrationService))

        .create()

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
//        val execRepository = SqliteExecRepository(
//            SqliteExecRepository.Config(
//                path = Path("/tmp/hamal"),
//                partition = Partition(1)
//            )
//        )
    }

}


