package io.hamal.api.http.snippet

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.domain.vo.SnippetInputs
import io.hamal.lib.domain.vo.SnippetName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.api.ApiUpdateSnippetReq
import org.junit.jupiter.api.Test
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiCreateSnippetReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal class SnippetUpdateControllerTest : SnippetBaseControllerTest() {
    @Test
    fun `Updates snippet`() {
        val snippet = awaitCompleted(
            createSnippet(
                ApiCreateSnippetReq(
                    name = SnippetName("TestSnippet"),
                    value = CodeValue("40 + 2"),
                    inputs = SnippetInputs()
                )
            )
        )

        val updateSnippetResponse = httpTemplate.patch("/v1/snippets/{snippedId}/update")
            .path("snippedId", snippet.id)
            .body(
                ApiUpdateSnippetReq(
                    name = SnippetName("Other"),
                    value = CodeValue("1 + 1"),
                    inputs = SnippetInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs"))))
                )
            )
            .execute()

        assertThat(updateSnippetResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(updateSnippetResponse is SuccessHttpResponse) { "request was not successful" }

        val submittedReq = updateSnippetResponse.result(ApiSubmittedReqWithId::class)
        awaitCompleted(submittedReq)

        val snippetId = submittedReq.id(::SnippetId)

        with(getSnippet(snippetId)) {
            assertThat(id, equalTo(snippetId))
            assertThat(name, equalTo(SnippetName("Other")))
            assertThat(value, equalTo(CodeValue("1 + 1")))
            assertThat(inputs, equalTo(SnippetInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs"))))))
        }
    }

    @Test
    fun `Tries to update snippet that does not exist`() {
        val getUpdateResponse = httpTemplate.patch("/v1/snippets/333333/update")
            .body(
                ApiUpdateSnippetReq(
                    name = SnippetName("TestSnippet"),
                    value = CodeValue("40 + 2"),
                    inputs = SnippetInputs()
                )
            )
            .execute()

        assertThat(getUpdateResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getUpdateResponse is ErrorHttpResponse) { "request was successful" }

        val error = getUpdateResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Snippet not found"))
    }
}