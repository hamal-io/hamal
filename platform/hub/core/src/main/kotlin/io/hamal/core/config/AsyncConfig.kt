package io.hamal.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar


@Async
@Configuration
@EnableScheduling
open class AsyncConfig : SchedulingConfigurer {
    @Bean
    open fun executor(): ThreadPoolTaskScheduler {
        val result = ThreadPoolTaskScheduler()
        result.threadNamePrefix = "backend-"
        result.poolSize = 1
        result.initialize()
        return result
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val scheduler = executor()

        taskRegistrar.setScheduler(scheduler)
        taskRegistrar.setTaskScheduler(scheduler)
    }
}