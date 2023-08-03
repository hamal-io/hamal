package io.hamal.runner.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Component
class RunnerAsync(
    @Autowired val agentExecutor: ThreadPoolTaskScheduler,
) {

    fun atFixedRate(period: Duration, task: Runnable): ScheduledFuture<*> {
        return agentExecutor.scheduleAtFixedRate(task, period.toJavaDuration())
    }
}