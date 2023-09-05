package io.hamal.api.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Component
class Async(
    @Autowired val executor: ThreadPoolTaskScheduler,
) {
    fun runAsync(runnable: Runnable): CompletableFuture<Unit> {
        return CompletableFuture.runAsync(runnable, executor).thenApply { }
    }

    fun atFixedRate(period: Duration, task: Runnable): ScheduledFuture<*> {
        return executor.scheduleAtFixedRate(task, period.toJavaDuration())
    }
}