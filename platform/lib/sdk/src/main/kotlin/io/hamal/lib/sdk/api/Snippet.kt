package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.request.CreateSnippetReq
import io.hamal.request.UpdateSnippetReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiSnippet(
    val id: SnippetId,
    val name: SnippetName,
    val inputs: SnippetInputs,
    val value: CodeValue
)

@Serializable
data class ApiCreateSnippetReq(
    override val name: SnippetName,
    override val inputs: SnippetInputs,
    override val value: CodeValue
) : CreateSnippetReq

@Serializable
data class ApiUpdateSnippetReq(
    override val name: SnippetName,
    override val inputs: SnippetInputs,
    override val value: CodeValue
) : UpdateSnippetReq

@Serializable
data class ApiSnippetList(
    val snippets: List<Snippet>
) {
    @Serializable
    data class Snippet(
        val id: SnippetId,
        val name: SnippetName
    )
}

interface ApiSnippetService {
    fun create(groupId: GroupId, createSnippetReq: ApiCreateSnippetReq): ApiSubmittedReqWithId
    fun list(groupId: GroupId): List<ApiSnippetList.Snippet>
    fun get(snippetId: SnippetId): ApiSnippet
}

internal class ApiSnippetServiceImpl(
    private val template: HttpTemplate
) : ApiSnippetService {
    override fun create(groupId: GroupId, createSnippetReq: ApiCreateSnippetReq): ApiSubmittedReqWithId {
        TODO("Not yet implemented")
    }

    override fun list(groupId: GroupId): List<ApiSnippetList.Snippet> {
        TODO("Not yet implemented")
    }

    override fun get(snippetId: SnippetId): ApiSnippet {
        TODO("Not yet implemented")
    }
}



