package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.ExtensionCreateRequest
import io.hamal.lib.domain.request.ExtensionCreateRequested
import io.hamal.lib.domain.request.ExtensionUpdateRequest
import io.hamal.lib.domain.request.ExtensionUpdateRequested
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.CodeQueryRepository
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionQueryRepository
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface ExtensionExtensionPort {
    operator fun invoke(
        workspaceId: WorkspaceId,
        req: ExtensionCreateRequest
    ): ExtensionCreateRequested
}

interface ExtensionGetPort {
    operator fun invoke(extId: ExtensionId): Extension
}

interface ExtensionListPort {
    operator fun invoke(query: ExtensionQuery): List<Extension>
}

interface ExtensionUpdatePort {
    operator fun invoke(
        extId: ExtensionId,
        req: ExtensionUpdateRequest
    ): ExtensionUpdateRequested
}

interface ExtensionPort : ExtensionExtensionPort, ExtensionGetPort, ExtensionListPort, ExtensionUpdatePort

@Component
class ExtensionAdapter(
    private val codeQueryRepository: CodeQueryRepository,
    private val extensionQueryRepository: ExtensionQueryRepository,
    private val generateDomainId: GenerateId,
    private val requestCmdRepository: RequestCmdRepository
) : ExtensionPort {
    override fun invoke(
        workspaceId: WorkspaceId,
        req: ExtensionCreateRequest
    ): ExtensionCreateRequested {
        return ExtensionCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            workspaceId = workspaceId,
            extensionId = generateDomainId(::ExtensionId),
            name = req.name,
            codeId = generateDomainId(::CodeId),
            code = req.code

        ).also(requestCmdRepository::queue)
    }

    override fun invoke(extId: ExtensionId): Extension = extensionQueryRepository.get(extId)

    override fun invoke(
        extId: ExtensionId,
        req: ExtensionUpdateRequest
    ): ExtensionUpdateRequested {
        ensureExtensionExists(extId)
        return ExtensionUpdateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            workspaceId = extensionQueryRepository.get(extId).workspaceId,
            extensionId = extId,
            name = req.name,
            code = req.code
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(query: ExtensionQuery): List<Extension> = extensionQueryRepository.list(query)

    private fun ensureExtensionExists(extId: ExtensionId) {
        extensionQueryRepository.get(extId)
    }
}