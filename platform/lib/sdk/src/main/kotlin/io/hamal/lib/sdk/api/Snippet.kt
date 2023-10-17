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
    val value: CodeValue,
    val accountId: AccountId
)

@Serializable
data class ApiCreateSnippetReq(
    override val name: SnippetName,
    override val inputs: SnippetInputs,
    override val value: CodeValue,
    override val accountId: AccountId
) : CreateSnippetReq

@Serializable
data class ApiUpdateSnippetReq(
    override val name: SnippetName,
    override val inputs: SnippetInputs,
    override val value: CodeValue
) : UpdateSnippetReq


interface ApiSnippetService {
    fun create(groupId: GroupId, createSnippetReq: ApiCreateSnippetReq): ApiSubmittedReqWithId
    fun get(snippetId: SnippetId): ApiSnippet
}

internal class ApiSnippetServiceImpl(
    private val template: HttpTemplate
) : ApiSnippetService {
    override fun create(groupId: GroupId, createSnippetReq: ApiCreateSnippetReq): ApiSubmittedReqWithId {
        TODO("Not yet implemented")
    }

    override fun get(snippetId: SnippetId): ApiSnippet {
        TODO("Not yet implemented")
    }
}



