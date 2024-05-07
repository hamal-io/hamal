package io.hamal.core.adapter.trigger

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.TriggerDeleteRequested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import io.hamal.lib.domain.vo.TriggerId
import org.springframework.stereotype.Component

fun interface TriggerDeletePort {
    operator fun invoke(triggerId: TriggerId): TriggerDeleteRequested
}

@Component
class TriggerDeleteAdapter(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort,
    private val triggerGetPort: TriggerGetPort
) : TriggerDeletePort {
    override fun invoke(triggerId: TriggerId): TriggerDeleteRequested {
        triggerGetPort(triggerId)

        return TriggerDeleteRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            id = triggerId
        ).also(requestEnqueue::invoke)
    }
}