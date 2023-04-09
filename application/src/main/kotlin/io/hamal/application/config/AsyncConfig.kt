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
open class AsyncConfig : SchedulingConfigurer {

    @Bean
    fun asyncExecutor(): Executor {
        val executor = ThreadPoolTaskScheduler()
//        executor.corePoolSize = 1
//        executor.maxPoolSize = 1
//        executor.queueCapacity = 2
        executor.threadNamePrefix = "MyExecutor-"
        executor.poolSize = 2
        
        executor.initialize()
        return executor
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(asyncExecutor());
    }

}