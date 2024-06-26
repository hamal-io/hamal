package io.hamal.core.adapter.adhoc

import io.hamal.core.adapter.namespace.NamespaceGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.AdhocInvokeRequest
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import org.springframework.stereotype.Component

fun interface AdhocInvokePort {
    operator fun invoke(namespaceId: NamespaceId, req: AdhocInvokeRequest): ExecInvokeRequested
}

@Component
class AdhocInvokeAdapter(
    private val generateDomainId: GenerateDomainId,
    private val namespaceGet: NamespaceGetPort,
    private val requestEnqueue: RequestEnqueuePort
) : AdhocInvokePort {
    override operator fun invoke(namespaceId: NamespaceId, req: AdhocInvokeRequest): ExecInvokeRequested {
        val namespace = namespaceGet(namespaceId)
        return ExecInvokeRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            id = generateDomainId(::ExecId),
            triggerId = null,
            namespaceId = namespace.id,
            workspaceId = namespace.workspaceId,
            inputs = req.inputs ?: InvocationInputs(),
            code = ExecCode(
                value = req.code,
                type = req.codeType
            ),
            funcId = null,
            correlationId = null
        ).also(requestEnqueue::invoke)
    }
}
