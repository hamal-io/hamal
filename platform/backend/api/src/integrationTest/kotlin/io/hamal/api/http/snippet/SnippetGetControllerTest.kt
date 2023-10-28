package io.hamal.api.http.snippet

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.SnippetInputs
import io.hamal.lib.domain.vo.SnippetName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiCreateSnippetReq
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiSnippet
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class SnippetGetControllerTest : SnippetBaseControllerTest() {

    @Test
    fun `Get snippet`() {
        val snippetId = createSnippet(
            ApiCreateSnippetReq(
                name = SnippetName("TestSnippet"),
                inputs = SnippetInputs(MapType(mutableMapOf("hamal" to StringType("rockz")))),
                value = CodeValue("1 + 1")
            )
        ).snippetId

        val getSnippetResponse = httpTemplate.get("/v1/snippets/{snippetId}")
            .path("snippetId", snippetId)
            .execute()


        assertThat(getSnippetResponse.statusCode, equalTo(Ok))
        require(getSnippetResponse is HttpSuccessResponse) { "request was not successful" }

        with(getSnippetResponse.result(ApiSnippet::class)) {
            assertThat(id, equalTo(snippetId))
            assertThat(name, equalTo(SnippetName("TestSnippet")))
            assertThat(inputs, equalTo(SnippetInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
            assertThat(value, equalTo(CodeValue("1 + 1")))
        }
    }

    @Test
    fun `Tries to get snippet that does not exist`() {
        val getSnippetResponse = httpTemplate.get("/v1/snippets/33333333").execute()
        assertThat(getSnippetResponse.statusCode, equalTo(NotFound))
        require(getSnippetResponse is HttpErrorResponse) { "request was successful" }

        val error = getSnippetResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Snippet not found"))
    }
}