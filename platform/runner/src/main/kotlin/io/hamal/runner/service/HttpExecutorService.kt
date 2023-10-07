package io.hamal.runner.service

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.BridgeSdkImpl
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.HttpConnector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.CodeRunnerImpl
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

@Service
class HttpExecutorService(
    private val httpTemplate: HttpTemplate,
    private val runnerExecutor: ThreadPoolTaskScheduler,
    private val sandboxFactory: SandboxFactory
) : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val sdk = BridgeSdkImpl(httpTemplate)
        val connector = HttpConnector(sdk)

        runnerExecutor.scheduleAtFixedRate({
            connector.poll().forEach { uow ->
                CodeRunnerImpl(connector, sandboxFactory).run(
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

