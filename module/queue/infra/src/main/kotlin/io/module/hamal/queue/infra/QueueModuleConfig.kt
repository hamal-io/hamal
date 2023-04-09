package io.module.hamal.queue.infra

import io.hamal.lib.domain_notification.HandleDomainNotificationPort
import io.hamal.lib.domain_notification.notification.JobDomainNotification
import io.module.hamal.queue.infra.handler.JobScheduledHandler
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling


@Configuration
@ComponentScan
@EnableScheduling
@EnableAutoConfiguration(exclude = [SpringApplicationAdminJmxAutoConfiguration::class, JmxAutoConfiguration::class])
open class QueueModuleConfig {

    //    @Bean
//    open fun processor(
//        receiveDomainNotificationPort: ReceiveDomainNotificationPort
//    ): ReceiveDomainNotificationPort {
//        return receiveDomainNotificationPort.register(JobDomainNotification.Scheduled::class, JobScheduledReceiver())
//    }
//
    @Autowired
    private lateinit var handleDomainNotificationPort: HandleDomainNotificationPort

//    override fun onApplicationEvent(event: ContextRefreshedEvent) {
//        receiveDomainNotificationPort.register(JobDomainNotification.Scheduled::class, JobScheduledReceiver())
//    }

    @PostConstruct
    fun run() {
        handleDomainNotificationPort.register(JobDomainNotification.Scheduled::class, JobScheduledHandler())
    }

}
