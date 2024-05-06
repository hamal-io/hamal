package io.hamal.api.http.controller.request

import io.hamal.lib.domain._enum.RequestStatuses.Completed
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiExecInvokeRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class RequestGetControllerTest : RequestBaseControllerTest() {
    @Test
    fun `Gets requests`() {
        val request = awaitCompleted(
            adhoc()
        )

        val response = httpTemplate.get("/v1/requests/{reqId}").path("reqId", request.requestId).execute()
        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }

        val result = response.result(ApiExecInvokeRequested::class)
        assertThat(result.requestStatus.enumValue, equalTo(Completed))
    }

    @Test
    fun `Tries to get request which does not exist`() {
        val response = httpTemplate.get("/v1/requests/123456765432").execute()
        assertThat(response.statusCode, equalTo(NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Request not found"))
    }

}