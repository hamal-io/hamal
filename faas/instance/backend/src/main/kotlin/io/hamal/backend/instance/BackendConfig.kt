package io.hamal.backend.instance

import io.hamal.backend.instance.component.BootstrapBackend
import io.hamal.backend.instance.event.*
import io.hamal.backend.instance.event.handler.exec.*
import io.hamal.backend.instance.event.handler.trigger.TriggerCreatedHandler
import io.hamal.backend.instance.service.FixedRateTriggerService
import io.hamal.backend.instance.service.OrchestrationService
import io.hamal.backend.instance.service.SystemEventServiceFactory
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
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
    open fun backendEventConsumer(
        eventServiceFactory: SystemEventServiceFactory,
        execQueryRepository: io.hamal.repository.api.ExecQueryRepository,
        execCmdRepository: io.hamal.repository.api.ExecCmdRepository,
        fixedRateTriggerService: FixedRateTriggerService,
        orchestrationService: OrchestrationService,
        eventEmitter: SystemEventEmitter
    ) = eventServiceFactory
        .register(TriggerCreatedEvent::class, TriggerCreatedHandler(fixedRateTriggerService))

        .register(ExecPlannedEvent::class, ExecPlannedHandler(orchestrationService))
        .register(ExecScheduledEvent::class, ExecScheduledHandler(execCmdRepository, eventEmitter))
        .register(ExecutionQueuedEvent::class, ExecQueuedHandler())
        .register(ExecutionCompletedEvent::class, ExecCompletedHandler(orchestrationService))
        .register(ExecutionFailedEvent::class, ExecFailedHandler(orchestrationService))

        .create()


    @Bean
    open fun commandLineRunner() = object : CommandLineRunner {

        @Autowired
        lateinit var backendBootstrap: BootstrapBackend

        override fun run(vararg args: String?) {
            backendBootstrap.bootstrap()
        }
    }
}


