package io.hamal.core.adapter.extension

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExtensionUpdateRequest
import io.hamal.lib.domain.request.ExtensionUpdateRequested
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface ExtensionUpdatePort {
    operator fun invoke(extensionId: ExtensionId, req: ExtensionUpdateRequest): ExtensionUpdateRequested
}

@Component
class ExtensionUpdateAdapter(
    private val extensionGet: ExtensionGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : ExtensionUpdatePort {
    override fun invoke(extensionId: ExtensionId, req: ExtensionUpdateRequest): ExtensionUpdateRequested {
        val extension = extensionGet(extensionId)
        return ExtensionUpdateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            workspaceId = extension.workspaceId,
            id = extension.id,
            name = req.name,
            code = req.code
        ).also(requestEnqueue::invoke)
    }
}
