package io.hamal.core.config

import io.hamal.core.component.Scheduler
import io.hamal.core.component.WorkerPool
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.Executors


@Async
@Configuration
@EnableScheduling
open class AsyncConfig : SchedulingConfigurer {

    @Bean
    open fun workerPoolExecutor(): ThreadPoolTaskScheduler {
        val result = ThreadPoolTaskScheduler()
        result.threadNamePrefix = "worker-"
        result.poolSize = 10
        result.initialize()
        return result
    }

    @Bean
    open fun workerPool(): WorkerPool {
        return WorkerPool(workerPoolExecutor())
    }

    @Bean
    open fun scheduler(): Scheduler {
        return Scheduler(Executors.newSingleThreadScheduledExecutor())
    }

    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    open fun asyncTaskExecutor(): AsyncTaskExecutor {
        return TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val scheduler = workerPoolExecutor()
        taskRegistrar.setScheduler(scheduler)
        taskRegistrar.setTaskScheduler(scheduler)
    }
}