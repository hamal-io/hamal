package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.*
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import io.hamal.lib.domain.submitted.ExtensionCreateSubmitted
import io.hamal.lib.domain.submitted.ExtensionUpdateSubmitted
import io.hamal.request.ExtensionCreateReq
import io.hamal.request.ExtensionUpdateReq
import org.springframework.stereotype.Component

interface ExtensionExtensionPort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: ExtensionCreateReq,
        responseHandler: (ExtensionCreateSubmitted) -> T
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
        req: ExtensionUpdateReq,
        responseHandler: (ExtensionUpdateSubmitted) -> T
    ): T
}

interface ExtensionPort : ExtensionExtensionPort, ExtensionGetPort, ExtensionListPort, ExtensionUpdatePort

@Component
class ExtensionAdapter(
    private val codeQueryRepository: CodeQueryRepository,
    private val extensionQueryRepository: ExtensionQueryRepository,
    private val generateDomainId: GenerateId,
    private val reqCmdRepository: ReqCmdRepository
) : ExtensionPort {
    override fun <T : Any> invoke(
        groupId: GroupId,
        req: ExtensionCreateReq,
        responseHandler: (ExtensionCreateSubmitted) -> T
    ): T {
        return ExtensionCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            groupId = groupId,
            extensionId = generateDomainId(::ExtensionId),
            name = req.name,
            codeId = generateDomainId(::CodeId),
            code = req.code

        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(extId: ExtensionId, responseHandler: (Extension, Code) -> T): T {
        val ext = extensionQueryRepository.get(extId)
        val code = codeQueryRepository.get(ext.code.id)
        return responseHandler(ext, code)
    }

    override fun <T : Any> invoke(
        extId: ExtensionId,
        req: ExtensionUpdateReq,
        responseHandler: (ExtensionUpdateSubmitted) -> T
    ): T {
        ensureExtensionExists(extId)
        return ExtensionUpdateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            groupId = extensionQueryRepository.get(extId).groupId,
            extensionId = extId,
            name = req.name,
            code = req.code
        ).also(reqCmdRepository::queue).let(responseHandler)
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