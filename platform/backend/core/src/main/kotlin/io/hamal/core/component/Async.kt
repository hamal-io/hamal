package io.hamal.core.component

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Component
class Async(private val executor: ThreadPoolTaskScheduler) {
    fun atFixedRate(period: Duration, task: Runnable): ScheduledFuture<*> {
        return executor.scheduleAtFixedRate(task, period.toJavaDuration())
    }
}