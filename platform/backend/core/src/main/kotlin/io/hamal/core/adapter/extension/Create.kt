package io.hamal.core.adapter.extension

import io.hamal.core.adapter.RequestEnqueuePort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExtensionCreateRequest
import io.hamal.lib.domain.request.ExtensionCreateRequested
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.WorkspaceId
import org.springframework.stereotype.Component

fun interface ExtensionCreatePort {
    operator fun invoke(workspaceId: WorkspaceId, req: ExtensionCreateRequest): ExtensionCreateRequested
}

@Component
class ExtensionCreateAdapter(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : ExtensionCreatePort {
    override fun invoke(workspaceId: WorkspaceId, req: ExtensionCreateRequest): ExtensionCreateRequested {
        return ExtensionCreateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            workspaceId = workspaceId,
            extensionId = generateDomainId(::ExtensionId),
            name = req.name,
            codeId = generateDomainId(::CodeId),
            code = req.code

        ).also(requestEnqueue::invoke)
    }
}
