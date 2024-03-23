package io.hamal.core.service

import io.hamal.core.adapter.func.FuncInvokePort
import io.hamal.core.component.WorkerPool
import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.request.FuncInvokeRequest
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.Invocation
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.Auth
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Service
internal class FixedRateTriggerService(
    private val workerPool: WorkerPool,
    internal val eventEmitter: InternalEventEmitter,
    internal val funcQueryRepository: FuncQueryRepository,
    internal val generateDomainId: GenerateDomainId,
    internal val funcInvoke: FuncInvokePort,
    internal val triggerQueryRepository: TriggerQueryRepository,
) : DisposableBean {

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()


    @PostConstruct
    fun setup() {
        triggerQueryRepository.list(
            TriggerQueryRepository.TriggerQuery(
                afterId = TriggerId(SnowflakeId(Long.MAX_VALUE)),
                limit = Limit(10),
                workspaceIds = listOf()
            )
        ).filterIsInstance<Trigger.FixedRate>().forEach { trigger -> triggerAdded(trigger) }
    }

    fun triggerAdded(trigger: Trigger.FixedRate) {
        scheduledTasks.add(
            workerPool.atFixedRate(Duration.parseIsoString(trigger.duration.value).inWholeSeconds.seconds) {
                requestInvocation(trigger)
            }
        )
    }

    override fun destroy() {
        scheduledTasks.forEach {
            it.cancel(true)
        }
    }
}

internal fun FixedRateTriggerService.requestInvocation(trigger: Trigger.FixedRate) {
    SecurityContext.with(Auth.System) {
        funcInvoke(
            trigger.funcId,
            object : FuncInvokeRequest {
                override val correlationId = trigger.correlationId ?: CorrelationId.default
                override val inputs = InvocationInputs()
                override val version = null
            },
            Invocation.Schedule
        )
    }
}