package io.hamal.backend.infra.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.Executor


@Async
@Configuration
@EnableScheduling
open class AsyncConfig : SchedulingConfigurer {

    @Bean
    open fun taskScheduler(): Executor {
        val result = ThreadPoolTaskScheduler()
        result.threadNamePrefix = "h4m41-"
        result.poolSize = 2
        result.initialize()
        return result
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }

}