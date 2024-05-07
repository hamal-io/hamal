package io.hamal.core.adapter.func

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.FuncUpdateRequest
import io.hamal.lib.domain.request.FuncUpdateRequested
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import org.springframework.stereotype.Component

fun interface FuncUpdatePort {
    operator fun invoke(funcId: FuncId, req: FuncUpdateRequest): FuncUpdateRequested
}

@Component
class FuncUpdateAdapter(
    private val funcGet: FuncGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : FuncUpdatePort {
    override fun invoke(funcId: FuncId, req: FuncUpdateRequest): FuncUpdateRequested {
        val func = funcGet(funcId)
        return FuncUpdateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            workspaceId = func.workspaceId,
            id = funcId,
            name = req.name,
            inputs = req.inputs,
            code = req.code
        ).also(requestEnqueue::invoke)
    }
}