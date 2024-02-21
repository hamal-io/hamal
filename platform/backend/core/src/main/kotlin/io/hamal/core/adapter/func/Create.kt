package io.hamal.core.adapter.func

import io.hamal.core.adapter.NamespaceGetPort
import io.hamal.core.adapter.RequestEnqueuePort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.FuncCreateRequest
import io.hamal.lib.domain.request.FuncCreateRequested
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
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
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            workspaceId = namespace.workspaceId,
            funcId = generateDomainId(::FuncId),
            namespaceId = namespaceId,
            name = req.name,
            inputs = req.inputs,
            codeId = generateDomainId(::CodeId),
            code = req.code
        ).also(requestEnqueue::invoke)
    }
}
