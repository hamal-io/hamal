package io.hamal.api.http.snippet

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiCreateSnippetReq
import io.hamal.lib.sdk.api.ApiSnippet
import io.hamal.lib.sdk.api.ApiSnippetCreateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class SnippetBaseControllerTest : BaseControllerTest() {
    fun createSnippet(req: ApiCreateSnippetReq): ApiSnippetCreateSubmitted {
        val createSnippetResponse = httpTemplate.post("/v1/groups/{groupId}/snippets")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(createSnippetResponse.statusCode, equalTo(Accepted))
        require(createSnippetResponse is HttpSuccessResponse) { "request was not successful" }
        return createSnippetResponse.result(ApiSnippetCreateSubmitted::class)

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