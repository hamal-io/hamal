package io.hamal.api.http.snippet

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.domain.vo.SnippetInputs
import io.hamal.lib.domain.vo.SnippetName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiCreateSnippetReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import io.hamal.lib.sdk.api.ApiUpdateSnippetReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@Suppress("UNCHECKED_CAST")
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

        val submittedReq = updateSnippetResponse.result(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<SnippetId>
        awaitCompleted(submittedReq)

        val snippetId = submittedReq.id

        with(getSnippet(snippetId)) {
            assertThat(id, equalTo(snippetId))
            assertThat(name, equalTo(SnippetName("Other")))
            assertThat(value, equalTo(CodeValue("1 + 1")))
            assertThat(inputs, equalTo(SnippetInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs"))))))
        }
    }

    @Test
    fun `Updates snippet without updating values`() {
        val snippet = awaitCompleted(
            createSnippet(
                ApiCreateSnippetReq(
                    name = SnippetName("TestSnippet"),
                    value = CodeValue("40 + 2"),
                    inputs = SnippetInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs"))))
                )
            )
        )

        val updateSnippetResponse = httpTemplate.patch("/v1/snippets/{snippedId}/update")
            .path("snippedId", snippet.id)
            .body(
                ApiUpdateSnippetReq(
                    name = null,
                    value = null,
                    inputs = null
                )
            )
            .execute()

        assertThat(updateSnippetResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(updateSnippetResponse is SuccessHttpResponse) { "request was not successful" }

        val submittedReq = updateSnippetResponse.result(ApiSubmittedReqImpl::class) as ApiSubmittedReqImpl<SnippetId>
        awaitCompleted(submittedReq)

        val snippetId = submittedReq.id

        with(getSnippet(snippetId)) {
            assertThat(id, equalTo(snippetId))
            assertThat(name, equalTo(SnippetName("TestSnippet")))
            assertThat(value, equalTo(CodeValue("40 + 2")))
            assertThat(inputs, equalTo(SnippetInputs(MapType(mutableMapOf("hamal" to StringType("createdInputs"))))))
        }
    }


    @Test
    fun `Tries to update snippet that does not exist`() {
        val updateResponse = httpTemplate.patch("/v1/snippets/333333/update")
            .body(
                ApiUpdateSnippetReq(
                    name = SnippetName("TestSnippet"),
                    value = CodeValue("40 + 2"),
                    inputs = SnippetInputs()
                )
            )
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(updateResponse is ErrorHttpResponse) { "request was successful" }

        val error = updateResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Snippet not found"))
    }
}