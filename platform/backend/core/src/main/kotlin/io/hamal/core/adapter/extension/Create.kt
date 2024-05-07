package io.hamal.core.adapter.extension

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.adapter.workspace.WorkspaceGetPort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.ExtensionCreateRequest
import io.hamal.lib.domain.request.ExtensionCreateRequested
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import io.hamal.lib.domain.vo.WorkspaceId
import org.springframework.stereotype.Component

fun interface ExtensionCreatePort {
    operator fun invoke(workspaceId: WorkspaceId, req: ExtensionCreateRequest): ExtensionCreateRequested
}

@Component
class ExtensionCreateAdapter(
    private val generateDomainId: GenerateDomainId,
    private val workspaceGet: WorkspaceGetPort,
    private val requestEnqueue: RequestEnqueuePort
) : ExtensionCreatePort {
    override fun invoke(workspaceId: WorkspaceId, req: ExtensionCreateRequest): ExtensionCreateRequested {
        val workspace = workspaceGet(workspaceId)
        return ExtensionCreateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            workspaceId = workspace.id,
            id = generateDomainId(::ExtensionId),
            name = req.name,
            codeId = generateDomainId(::CodeId),
            code = req.code

        ).also(requestEnqueue::invoke)
    }
}
