package io.hamal.runner.service

import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.BridgeSdkImpl
import io.hamal.runner.config.EnvFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.HttpConnector
import io.hamal.runner.run.CodeRunnerImpl
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ApplicationContextEvent
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

@Service
class HttpExecutorService(
    private val httpTemplate: HttpTemplateImpl,
    private val runnerExecutor: ThreadPoolTaskScheduler,
    private val sandboxFactory: SandboxFactory,
    private val runnerEnvFactory: EnvFactory,
    @Value("\${io.hamal.runner.http.poll-every-ms}") private val pollEveryMs: Long
) : ApplicationListener<ApplicationContextEvent>, DisposableBean {

    override fun onApplicationEvent(event: ApplicationContextEvent) {
        if (event is ContextRefreshedEvent) {
            val sdk = BridgeSdkImpl(httpTemplate)
            val connector = HttpConnector(sdk)

            scheduledTasks.add(
                runnerExecutor.scheduleAtFixedRate({
                    val unitsOfWork = connector.poll()
                    // FIXME core-60 -- backoff if empty or if exception got thrown
                    unitsOfWork.forEach { uow ->
                        CodeRunnerImpl(connector, sandboxFactory, runnerEnvFactory)
                            .run(uow)
                    }
                }, pollEveryMs.milliseconds.toJavaDuration())
            )
        }

        if (event is ContextClosedEvent) {
            destroy()
        }
    }

    override fun destroy() {
        scheduledTasks.forEach {
            it.cancel(true)
        }
    }

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()

}

