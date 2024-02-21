package io.hamal.core.adapter.adhoc

import io.hamal.core.adapter.NamespaceGetPort
import io.hamal.core.adapter.RequestEnqueuePort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.AdhocInvokeRequest
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.*
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
            id = generateDomainId(::RequestId),
            status = Submitted,
            execId = generateDomainId(::ExecId),
            namespaceId = namespace.id,
            workspaceId = namespace.workspaceId,
            inputs = req.inputs,
            code = ExecCode(value = req.code),
            funcId = null,
            correlationId = null,
            invocation = Invocation.Adhoc
        ).also(requestEnqueue::invoke)
    }
}
