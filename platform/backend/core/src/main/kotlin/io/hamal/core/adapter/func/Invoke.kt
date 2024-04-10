package io.hamal.core.adapter.func

import io.hamal.core.adapter.code.CodeGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.request.FuncInvokeRequest
import io.hamal.lib.domain.vo.*
import org.springframework.stereotype.Component

fun interface FuncInvokePort {
    operator fun invoke(funcId: FuncId, req: FuncInvokeRequest): ExecInvokeRequested
}

@Component
class FuncInvokeAdapter(
    private val funcGet: FuncGetPort,
    private val codeGet: CodeGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : FuncInvokePort {
    override fun invoke(funcId: FuncId, req: FuncInvokeRequest): ExecInvokeRequested {
        val func = funcGet(funcId)

        val code = req.version?.let { version -> codeGet(func.code.id, version) } ?: codeGet(func.code.id, func.code.version)

        return ExecInvokeRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = generateDomainId(::ExecId),
            triggerId = null,
            namespaceId = func.namespaceId,
            workspaceId = func.workspaceId,
            funcId = funcId,
            correlationId = req.correlationId,
            inputs = req.inputs ?: InvocationInputs(),
            code = ExecCode(
                id = code.id,
                version = code.version,
                value = code.value,
                type = code.type
            ),
        ).also(requestEnqueue::invoke)
    }
}
