package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.repository.api.Snippet
import io.hamal.repository.api.SnippetQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.CreateSnippetReq
import io.hamal.request.UpdateSnippetReq
import org.springframework.stereotype.Component

interface SnippetCreatePort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        accountId: AccountId,
        req: CreateSnippetReq,
        responseHandler: (SubmittedReq) -> T
    ): T
}

interface SnippetGetPort {
    operator fun <T : Any> invoke(snippetId: SnippetId, responseHandler: (Snippet) -> T): T
}


interface SnippetUpdatePort {
    operator fun <T : Any> invoke(
        snippetId: SnippetId,
        req: UpdateSnippetReq,
        responseHandler: (SubmittedReq) -> T
    ): T
}

interface SnippetPort : SnippetCreatePort, SnippetGetPort, SnippetUpdatePort

@Component
class SnippetAdapter(
    private val submitRequest: SubmitRequest,
    private val snippetQueryRepository: SnippetQueryRepository,
) : SnippetPort {
    override fun <T : Any> invoke(
        groupId: GroupId,
        accountId: AccountId,
        req: CreateSnippetReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        return responseHandler(submitRequest(groupId, accountId, req))
    }

    override fun <T : Any> invoke(snippetId: SnippetId, responseHandler: (Snippet) -> T): T {
        return responseHandler(snippetQueryRepository.get(snippetId))
    }

    override fun <T : Any> invoke(
        snippetId: SnippetId,
        req: UpdateSnippetReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        ensureSnippetExists(snippetId)
        return responseHandler(submitRequest(snippetId, req))
    }

    private fun ensureSnippetExists(snippetId: SnippetId) = snippetQueryRepository.get(snippetId)
}