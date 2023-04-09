package io.module.hamal.queue.infra

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


}
