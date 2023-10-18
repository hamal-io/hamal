package io.hamal.api.http.snippet

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.SnippetInputs
import io.hamal.lib.domain.vo.SnippetName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.sdk.api.ApiUpdateSnippetReq
import org.junit.jupiter.api.Test
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal class SnippetUpdateControllerTest : SnippetBaseControllerTest() {
    @Test
    fun `Tries to update snippet that does not exist`() {
        val getUpdateResponse = httpTemplate.patch("/v1/snippets/333333")
            .body(
                ApiUpdateSnippetReq(
                    name = SnippetName("TestSnippet"),
                    value = CodeValue("40 + 2"),
                    inputs = SnippetInputs()
                )
            )
            .execute()

        assertThat(getUpdateResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getUpdateResponse is ErrorHttpResponse){ "request was successful" }

        val error = getUpdateResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Snippet not found"))
    }

    @Test
    fun `Updates snippet`(){

    }
}