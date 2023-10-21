package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
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
    override val name: SnippetName? = null,
    override val inputs: SnippetInputs? = null,
    override val value: CodeValue? = null
) : UpdateSnippetReq


interface ApiSnippetService {
    fun create(groupId: GroupId, createSnippetReq: ApiCreateSnippetReq): ApiSubmittedReqWithId
    fun get(snippetId: SnippetId): ApiSnippet
    fun update(snippetId: SnippetId, updateSnippetReq: ApiUpdateSnippetReq): ApiSubmittedReqWithId
}

internal class ApiSnippetServiceImpl(
    private val template: HttpTemplate
) : ApiSnippetService {
    override fun create(groupId: GroupId, createSnippetReq: ApiCreateSnippetReq): ApiSubmittedReqWithId =
        template.post("/v1/groups/{groupId}/snippets")
            .path("groupId", groupId)
            .body(createSnippetReq)
            .execute()
            .fold(ApiSubmittedReqWithId::class)


    override fun get(snippetId: SnippetId): ApiSnippet =
        template.get("/v1/snippets/{snippetId}")
            .path("snippetId", snippetId)
            .execute()
            .fold(ApiSnippet::class)


    override fun update(snippetId: SnippetId, updateSnippetReq: ApiUpdateSnippetReq): ApiSubmittedReqWithId =
        template.patch("/v1/snippets/{snippedId}/update")
            .path("snippedId", snippetId)
            .body(updateSnippetReq)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

}



