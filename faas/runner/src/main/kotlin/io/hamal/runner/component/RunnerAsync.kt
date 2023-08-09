package io.hamal.runner.component

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Component
class RunnerAsync(
    private val runnerExecutor: ThreadPoolTaskScheduler,
) {

    fun atFixedRate(period: Duration, task: Runnable): ScheduledFuture<*> {
        return runnerExecutor.scheduleAtFixedRate(task, period.toJavaDuration())
    }
}