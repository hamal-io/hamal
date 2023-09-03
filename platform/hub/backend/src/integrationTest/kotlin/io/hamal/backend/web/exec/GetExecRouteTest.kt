package io.hamal.backend.web.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.hub.domain.ApiError
import io.hamal.lib.sdk.hub.domain.ApiExec
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

internal class GetExecRouteTest : BaseExecRouteTest() {
    @Test
    fun `Get exec`() {
        val createAdhocResponse = awaitCompleted(
            createAdhocExec()
        )

        val response = httpTemplate.get("/v1/execs/{execId}").path("execId", createAdhocResponse.id).execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse)

        with(response.result(ApiExec::class)) {
            assertThat(id, equalTo(createAdhocResponse.id(::ExecId)))
            assertThat(inputs, equalTo(ExecInputs()))
            assertThat(code, equalTo(CodeType("40 + 2")))
            assertThat(correlation, nullValue())
        }
    }

    @Test
    fun `Tries to get exec which does not exist`() {
        val response = httpTemplate.get("/v1/execs/123456765432").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Exec not found"))
    }
}