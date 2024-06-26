package io.hamal.core.adapter.func

import io.hamal.core.adapter.namespace.NamespaceGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.FuncCreateRequest
import io.hamal.lib.domain.request.FuncCreateRequested
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import org.springframework.stereotype.Component

fun interface FuncCreatePort {
    operator fun invoke(namespaceId: NamespaceId, req: FuncCreateRequest): FuncCreateRequested
}

@Component
class FuncCreateAdapter(
    private val namespaceGet: NamespaceGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : FuncCreatePort {
    override fun invoke(namespaceId: NamespaceId, req: FuncCreateRequest): FuncCreateRequested {
        val namespace = namespaceGet(namespaceId)
        return FuncCreateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            workspaceId = namespace.workspaceId,
            id = generateDomainId(::FuncId),
            namespaceId = namespaceId,
            name = req.name,
            inputs = req.inputs,
            codeId = generateDomainId(::CodeId),
            code = req.code,
            codeType = req.codeType
        ).also(requestEnqueue::invoke)
    }
}