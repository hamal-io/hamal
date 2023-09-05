package io.hamal.runner.service

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.DefaultHubSdk
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.HttpConnector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.DefaultCodeRunner
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

@Service
class HttpExecutorService(
    private val httpTemplate: HttpTemplate,
    private val runnerExecutor: ThreadPoolTaskScheduler,
    private val sandboxFactory: SandboxFactory
) {
    @Scheduled(initialDelay = 1, timeUnit = TimeUnit.SECONDS, fixedRate = Int.MAX_VALUE.toLong())
    fun run() {
        val sdk = DefaultHubSdk(httpTemplate)
        val connector = HttpConnector(sdk)

        runnerExecutor.scheduleAtFixedRate({
            connector.poll().forEach { uow ->
                DefaultCodeRunner(connector, sandboxFactory).run(
                    UnitOfWork(
                        id = uow.id,
                        groupId = uow.groupId,
                        inputs = uow.inputs,
                        state = uow.state,
                        code = uow.code,
                        correlation = uow.correlation,
                        events = uow.events
                    )
                )
            }
        }, 10.milliseconds.toJavaDuration())
    }
}

