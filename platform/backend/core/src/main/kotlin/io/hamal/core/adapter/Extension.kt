package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeQueryRepository
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionQueryRepository
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import io.hamal.repository.api.submitted_req.SubmittedReqWithGroupId
import io.hamal.request.CreateExtensionReq
import io.hamal.request.UpdateExtensionReq
import org.springframework.stereotype.Component

interface CreateExtensionPort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateExtensionReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface GetExtensionPort {
    operator fun <T : Any> invoke(extId: ExtensionId, responseHandler: (Extension, Code) -> T): T
}

interface ListExtensionPort {
    operator fun <T : Any> invoke(
        query: ExtensionQuery,
        responseHandler: (List<Extension>) -> T
    ): T
}

interface UpdateExtensionPort {
    operator fun <T : Any> invoke(
        extId: ExtensionId,
        req: UpdateExtensionReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface ExtensionPort : CreateExtensionPort, GetExtensionPort, ListExtensionPort, UpdateExtensionPort

@Component
class ExtensionAdapter(
    private val submitRequest: SubmitRequest,
    private val extensionQueryRepository: ExtensionQueryRepository,
    private val codeQueryRepository: CodeQueryRepository
) : ExtensionPort {
    override fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateExtensionReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T {
        return responseHandler(submitRequest(groupId, req))
    }

    override fun <T : Any> invoke(extId: ExtensionId, responseHandler: (Extension, Code) -> T): T {
        val ext = extensionQueryRepository.get(extId)
        val code = codeQueryRepository.get(ext.code.id)
        return responseHandler(ext, code)
    }

    override fun <T : Any> invoke(
        extId: ExtensionId,
        req: UpdateExtensionReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T {
        ensureExtensionExists(extId)
        return responseHandler(submitRequest(extId, req))
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