package io.hamal.core.adapter.hook

import io.hamal.core.adapter.RequestEnqueuePort
import io.hamal.core.adapter.namespace.NamespaceGetPort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.HookCreateRequest
import io.hamal.lib.domain.request.HookCreateRequested
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface HookCreatePort {
    operator fun invoke(namespaceId: NamespaceId, req: HookCreateRequest): HookCreateRequested
}

@Component
class HookCreateAdapter(
    private val namespaceGet: NamespaceGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : HookCreatePort {
    override fun invoke(namespaceId: NamespaceId, req: HookCreateRequest): HookCreateRequested {
        val namespace = namespaceGet(namespaceId)
        return HookCreateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            hookId = generateDomainId(::HookId),
            workspaceId = namespace.workspaceId,
            namespaceId = namespace.id,
            name = req.name
        ).also(requestEnqueue::invoke)
    }

}