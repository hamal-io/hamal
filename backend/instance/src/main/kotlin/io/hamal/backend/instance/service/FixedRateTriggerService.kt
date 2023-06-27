package io.hamal.backend.instance.service

import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.req.InvokeFixedRate
import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.instance.service.query.FuncQueryService
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.lib.common.util.TimeUtils.now
import io.hamal.lib.domain.FixedRateTrigger
import io.hamal.lib.domain.Trigger
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.port.GenerateDomainId
import io.hamal.lib.script.api.value.TableValue
import jakarta.annotation.PostConstruct
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
    internal val eventEmitter: SystemEventEmitter<*>,
    internal val submitRequest: SubmitRequest,
    internal val generateDomainId: GenerateDomainId
) {

    private val plannedInvocations = mutableMapOf<Trigger, Instant>()

    @PostConstruct
    fun setup() {
        triggerQueryRepository.list {
            afterId = TriggerId(0)
            limit = Limit(10)
        }.filterIsInstance<FixedRateTrigger>().forEach {
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
        InvokeFixedRate(
            execId = generateDomainId(::ExecId),
            funcId = trigger.funcId,
            correlationId = CorrelationId("__TBD__"), //FIXME
            inputs = InvocationInputs(TableValue()),
            secrets = InvocationSecrets(listOf()),
        )
    )
}