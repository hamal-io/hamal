package io.hamal.core.service

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.InvokeExecReq
import io.hamal.core.req.SubmitRequest
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.TimeUtils.now
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
internal class FixedRateTriggerService(
    internal val triggerQueryRepository: TriggerQueryRepository,
    internal val eventEmitter: PlatformEventEmitter,
    internal val submitRequest: SubmitRequest,
    internal val generateDomainId: GenerateDomainId,
    internal val funcQueryRepository: FuncQueryRepository
) {

    private val plannedInvocations = mutableMapOf<Trigger, Instant>()

    @PostConstruct
    fun setup() {
        triggerQueryRepository.list(
            TriggerQueryRepository.TriggerQuery(
                afterId = TriggerId(SnowflakeId(Long.MAX_VALUE)),
                limit = Limit(10),
                groupIds = listOf()
            )
        ).filterIsInstance<FixedRateTrigger>().forEach {
            plannedInvocations[it] = now().plusMillis(it.duration.inWholeSeconds)
        }
    }

    fun triggerAdded(fixedRateTrigger: FixedRateTrigger) {
        plannedInvocations.putIfAbsent(
            fixedRateTrigger,
            now().plusSeconds(fixedRateTrigger.duration.inWholeSeconds)
        )
    }


    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    fun run() {
        plannedInvocations.filter { now().isAfter(it.value) }.forEach { (trigger, _) ->
            require(trigger is FixedRateTrigger)
            plannedInvocations[trigger] = now().plusSeconds(trigger.duration.inWholeSeconds)
            requestInvocation(trigger)
        }
    }
}

internal fun FixedRateTriggerService.requestInvocation(trigger: FixedRateTrigger) {
    submitRequest(
        InvokeExecReq(
            execId = generateDomainId(::ExecId),
            funcId = trigger.funcId,
            correlationId = trigger.correlationId ?: CorrelationId("__default__"),
            inputs = InvocationInputs(),
            code = funcQueryRepository.get(trigger.funcId).code,
            events = listOf() // FIXME
        )
    )
}