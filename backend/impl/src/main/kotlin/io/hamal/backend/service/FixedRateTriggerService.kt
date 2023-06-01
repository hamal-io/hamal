package io.hamal.backend.service

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.domain.FixedRateTrigger
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.backend.req.Request
import io.hamal.backend.service.query.FuncQueryService
import io.hamal.lib.common.Shard
import io.hamal.lib.common.util.TimeUtils.now
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
class FixedRateTriggerService
@Autowired constructor(
    internal val funcQueryService: FuncQueryService,
    internal val triggerQueryRepository: TriggerQueryRepository,
    internal val eventEmitter: EventEmitter,
    internal val request: Request,
    internal val generateDomainId: GenerateDomainId
) {

    private val plannedInvocations = mutableMapOf<Trigger, Instant>()


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
            emitInvocation(trigger)
        }
    }
}

internal fun FixedRateTriggerService.emitInvocation(trigger: FixedRateTrigger) {
    request.invokeFixedRate(
        execId = generateDomainId(Shard(1), ::ExecId),
        func = funcQueryService.get(trigger.funcId),
        correlationId = CorrelationId("__TBD__"), //FIXME
        inputs = InvocationInputs(listOf()),
        secrets = InvocationSecrets(listOf()),
        trigger = trigger
    )
}