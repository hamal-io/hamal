package io.hamal.core.adapter.func

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.adapter.code.CodeGetPort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.FuncDeployRequest
import io.hamal.lib.domain.request.FuncDeployRequested
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.RequestId
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
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            workspaceId = func.workspaceId,
            funcId = funcId,
            version = req.version,
            message = req.message
        ).also(requestEnqueue::invoke)
    }
}