package io.hamal.core.adapter.func

import io.hamal.core.adapter.code.CodeGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.FuncDeployRequest
import io.hamal.lib.domain.request.FuncDeployRequested
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import org.springframework.stereotype.Component

fun interface FuncDeployPort {
    operator fun invoke(funcId: FuncId, req: FuncDeployRequest): FuncDeployRequested
}

@Component
class FuncDeployAdapter(
    private val funcGet: FuncGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort,
    private val codeGet: CodeGetPort
) : FuncDeployPort {
    override fun invoke(funcId: FuncId, req: FuncDeployRequest): FuncDeployRequested {
        val func = funcGet(funcId)
        req.version?.let { codeGet(func.code.id, req.version!!) }
        return FuncDeployRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            workspaceId = func.workspaceId,
            id = funcId,
            version = req.version,
            message = req.message
        ).also(requestEnqueue::invoke)
    }
}