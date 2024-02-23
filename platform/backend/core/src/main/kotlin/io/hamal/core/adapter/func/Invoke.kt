package io.hamal.core.adapter.func

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.adapter.code.CodeGetPort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.request.FuncInvokeRequest
import io.hamal.lib.domain.vo.*
import org.springframework.stereotype.Component

fun interface FuncInvokePort {
    operator fun invoke(funcId: FuncId, req: FuncInvokeRequest, invocation: Invocation): ExecInvokeRequested
}

@Component
class FuncInvokeAdapter(
    private val funcGet: FuncGetPort,
    private val codeGet: CodeGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : FuncInvokePort {
    override fun invoke(funcId: FuncId, req: FuncInvokeRequest, invocation: Invocation): ExecInvokeRequested {
        val func = funcGet(funcId)

        val version = req.version?.also {
            codeGet(func.code.id, it)
        } ?: func.code.version

        return ExecInvokeRequested(
            id = generateDomainId(::RequestId),
            by = SecurityContext.currentAuthId,
            status = RequestStatus.Submitted,
            execId = generateDomainId(::ExecId),
            namespaceId = func.namespaceId,
            workspaceId = func.workspaceId,
            funcId = funcId,
            correlationId = req.correlationId,
            inputs = req.inputs,
            code = ExecCode(
                id = func.code.id,
                version = version,
                value = null
            ),
            invocation = invocation
        ).also(requestEnqueue::invoke)
    }
}
