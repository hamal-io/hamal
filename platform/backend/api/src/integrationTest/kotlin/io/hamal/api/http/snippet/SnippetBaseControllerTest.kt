package io.hamal.api.http.snippet

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiCreateSnippetReq
import io.hamal.lib.sdk.api.ApiSnippet
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import io.hamal.lib.sdk.toReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

@Suppress("UNCHECKED_CAST")
internal sealed class SnippetBaseControllerTest : BaseControllerTest() {
    fun createSnippet(req: ApiCreateSnippetReq): ApiSubmittedReqImpl<SnippetId> {
        val createSnippetResponse = httpTemplate.post("/v1/groups/{groupId}/snippets")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(createSnippetResponse.statusCode, equalTo(Accepted))
        require(createSnippetResponse is HttpSuccessResponse) { "request was not successful" }
        return createSnippetResponse.toReq()
    }

    fun getSnippet(snippetId: SnippetId): ApiSnippet {
        val getSnippetResponse = httpTemplate.get("/v1/snippets/{snippetId}")
            .path("snippetId", snippetId)
            .execute()

        assertThat(getSnippetResponse.statusCode, equalTo(Ok))
        require(getSnippetResponse is HttpSuccessResponse) { "request was not successful" }
        return getSnippetResponse.result(ApiSnippet::class)
    }
}