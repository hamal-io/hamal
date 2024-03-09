package io.hamal.core.adapter.trigger

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain.request.TriggerStatusRequested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TriggerId
import org.springframework.stereotype.Component

fun interface TriggerSetStatusPort {
    operator fun invoke(triggerId: TriggerId, triggerStatus: TriggerStatus): TriggerStatusRequested
}

@Component
class TriggerSetStatusAdapter(
    private val triggerGet: TriggerGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : TriggerSetStatusPort {
    override fun invoke(triggerId: TriggerId, triggerStatus: TriggerStatus): TriggerStatusRequested {
        triggerGet(triggerId)
        return TriggerStatusRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = triggerId,
            status = triggerStatus
        ).also(requestEnqueue::invoke)
    }
}