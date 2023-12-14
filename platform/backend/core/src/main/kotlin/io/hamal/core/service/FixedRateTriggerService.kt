package io.hamal.core.service

import io.hamal.core.adapter.FuncInvokePort
import io.hamal.core.component.Async
import io.hamal.core.event.PlatformEventEmitter
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils.now
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.EmptyInvocation
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.request.FuncInvokeReq
import org.springframework.beans.factory.DisposableBean
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.seconds

@Service
internal class FixedRateTriggerService(
    private val async: Async,
    internal val eventEmitter: PlatformEventEmitter,
    internal val funcQueryRepository: FuncQueryRepository,
    internal val generateDomainId: GenerateDomainId,
    internal val invokeFunc: FuncInvokePort,
    internal val triggerQueryRepository: TriggerQueryRepository,
) : ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private val scheduledTasks = mutableListOf<ScheduledFuture<*>>()


    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        triggerQueryRepository.list(
            TriggerQueryRepository.TriggerQuery(
                afterId = TriggerId(SnowflakeId(Long.MAX_VALUE)),
                limit = Limit(10),
                groupIds = listOf()
            )
        ).filterIsInstance<FixedRateTrigger>()
            .forEach {
                plannedInvocations[it] = now().plusMillis(it.duration.inWholeSeconds)
            }

        scheduledTasks.add(
            async.atFixedRate(1.seconds) {
                plannedInvocations.filter { now().isAfter(it.value) }.forEach { (trigger, _) ->
                    require(trigger is FixedRateTrigger)
                    plannedInvocations[trigger] = now().plusSeconds(trigger.duration.inWholeSeconds)
                    requestInvocation(trigger)
                }
            }
        )
    }

    fun triggerAdded(fixedRateTrigger: FixedRateTrigger) {
        plannedInvocations.putIfAbsent(
            fixedRateTrigger,
            now().plusSeconds(fixedRateTrigger.duration.inWholeSeconds)
        )
    }

    private val plannedInvocations = mutableMapOf<Trigger, Instant>()

    override fun destroy() {
        scheduledTasks.forEach {
            it.cancel(false)
        }
    }

}

internal fun FixedRateTriggerService.requestInvocation(trigger: FixedRateTrigger) {
    invokeFunc(
        trigger.funcId,
        object : FuncInvokeReq {
            override val correlationId = trigger.correlationId ?: CorrelationId.default
            override val inputs = InvocationInputs()
            override val invocation = EmptyInvocation
        },
    ) {}
}