package io.hamal.core.adapter.state

import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.StateSetRequest
import io.hamal.lib.domain.request.StateSetRequested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import org.springframework.stereotype.Component

fun interface StateSetPort {
    operator fun invoke(req: StateSetRequest): StateSetRequested
}

@Component
class StateAdapter(
    private val generateDomainId: GenerateDomainId,
    private val funcGet: FuncGetPort,
    private val requestEnqueue: RequestEnqueuePort
) : StateSetPort {

    override operator fun invoke(req: StateSetRequest): StateSetRequested {
        val func = funcGet(req.correlation.funcId)
        return StateSetRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            workspaceId = func.workspaceId,
            state = CorrelatedState(
                correlation = req.correlation,
                value = req.value
            )
        ).also(requestEnqueue::invoke)
    }
}