package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.ExtensionCreateRequest
import io.hamal.lib.domain.request.ExtensionUpdateRequest
import io.hamal.lib.domain.request.ExtensionCreateRequested
import io.hamal.lib.domain.request.ExtensionUpdateRequested
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.*
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import org.springframework.stereotype.Component

interface ExtensionExtensionPort {
    operator fun <T : Any> invoke(
        workspaceId: WorkspaceId,
        req: ExtensionCreateRequest,
        responseHandler: (ExtensionCreateRequested) -> T
    ): T
}

interface ExtensionGetPort {
    operator fun <T : Any> invoke(extId: ExtensionId, responseHandler: (Extension, Code) -> T): T
}

interface ExtensionListPort {
    operator fun <T : Any> invoke(
        query: ExtensionQuery,
        responseHandler: (List<Extension>) -> T
    ): T
}

interface ExtensionUpdatePort {
    operator fun <T : Any> invoke(
        extId: ExtensionId,
        req: ExtensionUpdateRequest,
        responseHandler: (ExtensionUpdateRequested) -> T
    ): T
}

interface ExtensionPort : ExtensionExtensionPort, ExtensionGetPort, ExtensionListPort, ExtensionUpdatePort

@Component
class ExtensionAdapter(
    private val codeQueryRepository: CodeQueryRepository,
    private val extensionQueryRepository: ExtensionQueryRepository,
    private val generateDomainId: GenerateId,
    private val requestCmdRepository: RequestCmdRepository
) : ExtensionPort {
    override fun <T : Any> invoke(
        workspaceId: WorkspaceId,
        req: ExtensionCreateRequest,
        responseHandler: (ExtensionCreateRequested) -> T
    ): T {
        return ExtensionCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            workspaceId = workspaceId,
            extensionId = generateDomainId(::ExtensionId),
            name = req.name,
            codeId = generateDomainId(::CodeId),
            code = req.code

        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(extId: ExtensionId, responseHandler: (Extension, Code) -> T): T {
        val ext = extensionQueryRepository.get(extId)
        val code = codeQueryRepository.get(ext.code.id)
        return responseHandler(ext, code)
    }

    override fun <T : Any> invoke(
        extId: ExtensionId,
        req: ExtensionUpdateRequest,
        responseHandler: (ExtensionUpdateRequested) -> T
    ): T {
        ensureExtensionExists(extId)
        return ExtensionUpdateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            workspaceId = extensionQueryRepository.get(extId).workspaceId,
            extensionId = extId,
            name = req.name,
            code = req.code
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        query: ExtensionQuery,
        responseHandler: (List<Extension>) -> T
    ): T {
        return responseHandler(extensionQueryRepository.list(query))
    }

    private fun ensureExtensionExists(extId: ExtensionId) {
        extensionQueryRepository.get(extId)
    }
}