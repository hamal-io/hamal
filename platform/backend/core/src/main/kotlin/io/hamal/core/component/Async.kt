package io.hamal.core.component

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class WorkerPool(private val executor: ThreadPoolTaskScheduler) {
    fun atFixedRate(period: Duration, task: Runnable): ScheduledFuture<*> {
        return executor.scheduleWithFixedDelay(task, period.toJavaDuration())
    }

    fun atFixedDelay(period: Duration, task: Runnable): ScheduledFuture<*> {
        return executor.scheduleWithFixedDelay(task, period.toJavaDuration())
    }

    fun execute(task: Runnable) {
        executor.execute(task)
    }
}

class Scheduler(private val executor: ScheduledExecutorService) {
    fun atFixedRate(period: Duration, task: Runnable): ScheduledFuture<*> {
        val milliseconds = period.inWholeMilliseconds
        return executor.scheduleWithFixedDelay(task, milliseconds, milliseconds, TimeUnit.MILLISECONDS)
    }
}