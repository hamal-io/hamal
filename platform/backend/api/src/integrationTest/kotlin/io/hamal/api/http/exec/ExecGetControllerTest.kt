package io.hamal.api.http.exec

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiExec
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

internal class ExecGetControllerTest : ExecBaseControllerTest() {
    @Test
    fun `Get exec`() {
        val createAdhocResponse = awaitCompleted(createAdhocExec())

        val response = httpTemplate.get("/v1/execs/{execId}")
            .path("execId", createAdhocResponse.execId)
            .execute()

        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is HttpSuccessResponse)

        with(response.result(ApiExec::class)) {
            assertThat(id, equalTo(createAdhocResponse.execId))
            assertThat(inputs, equalTo(ExecInputs()))
            assertThat(
                code, equalTo(
                    ApiExec.Code(
                        id = null,
                        version = null,
                        value = CodeValue("40 + 2")
                    )
                )
            )
            assertThat(correlation, nullValue())
        }
    }

    @Test
    fun `Tries to get exec which does not exist`() {
        val response = httpTemplate.get("/v1/execs/123456765432").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Exec not found"))
    }
}