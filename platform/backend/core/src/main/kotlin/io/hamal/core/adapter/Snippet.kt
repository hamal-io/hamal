package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Snippet
import io.hamal.repository.api.SnippetQueryRepository
import io.hamal.repository.api.SnippetQueryRepository.*
import io.hamal.repository.api.submitted_req.SubmittedReqWithGroupId
import io.hamal.request.CreateSnippetReq
import io.hamal.request.UpdateSnippetReq
import org.springframework.stereotype.Component

interface CreateSnippetPort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateSnippetReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface GetSnippetPort {
    operator fun <T : Any> invoke(snippetId: SnippetId, responseHandler: (Snippet) -> T): T
}

interface UpdateSnippetPort {
    operator fun <T : Any> invoke(
        snippetId: SnippetId,
        req: UpdateSnippetReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface SnippetPort : CreateSnippetPort, GetSnippetPort, UpdateSnippetPort

@Component
class SnippetAdapter(
    private val submitRequest: SubmitRequest,
    private val snippetQueryRepository: SnippetQueryRepository,
    ) : SnippetPort {
    override fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateSnippetReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> invoke(snippetId: SnippetId, responseHandler: (Snippet) -> T): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> invoke(
        snippetId: SnippetId,
        req: UpdateSnippetReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T {
        TODO("Not yet implemented")
    }
}