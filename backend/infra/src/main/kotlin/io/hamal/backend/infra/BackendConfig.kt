package io.hamal.backend.infra

import io.hamal.backend.infra.adapter.CreateDomainNotificationProcessorPort
import io.hamal.backend.infra.module.orchestrator.handler.JobPlannedHandler
import io.hamal.backend.infra.module.orchestrator.handler.TriggerInvokedHandler
import io.hamal.backend.infra.module.queue.handler.JobQueuedHandler
import io.hamal.backend.infra.module.queue.handler.JobScheduledHandler
import io.hamal.backend.notification.JobPlannedNotification
import io.hamal.backend.notification.JobQueuedNotification
import io.hamal.backend.notification.JobScheduledNotification
import io.hamal.backend.notification.ManualTriggerInvokedNotification
import io.hamal.backend.usecase.request.JobDefinitionRequest
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.ddd.usecase.InvokeRequestOneUseCasePort
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
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
        .register(ManualTriggerInvokedNotification::class, TriggerInvokedHandler(invokeUseCasePort))
        .register(JobPlannedNotification::class, JobPlannedHandler(invokeUseCasePort))
        .register(JobScheduledNotification::class, JobScheduledHandler(invokeUseCasePort))
        .register(JobQueuedNotification::class, JobQueuedHandler())
        .create()

    @Autowired
    private lateinit var request: InvokeRequestOneUseCasePort

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val jobDef = request(
            JobDefinitionRequest.JobDefinitionCreation(
                requestId = RequestId(10000),
                shard = Shard(0)
            )
        )
        println(jobDef)
    }

}


