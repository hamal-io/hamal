package io.hamal.application.config

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
class AsyncConfig : SchedulingConfigurer {

    @Bean
    fun taskScheduler(): Executor {
        val executor = ThreadPoolTaskScheduler()
        executor.threadNamePrefix = "h4m41-"
        executor.poolSize = 2
        executor.initialize()
        return executor
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }

}