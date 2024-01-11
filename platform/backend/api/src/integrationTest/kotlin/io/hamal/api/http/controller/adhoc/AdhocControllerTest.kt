package io.hamal.api.http.controller.adhoc

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiAdhocInvokeRequest
import io.hamal.lib.sdk.api.ApiExecInvokeRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test


internal class AdhocControllerTest : BaseControllerTest() {
    @Test
    fun `Submits adhoc requests without inputs or secrets`() {
        val response = request(
            ApiAdhocInvokeRequest(
                inputs = InvocationInputs(),
                code = CodeValue("40 + 2")
            )
        )

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }

        val result = awaitCompleted(response.result(ApiExecInvokeRequested::class))
        assertThat(result.status, equalTo(Submitted))

        verifyReqCompleted(result.id)
        verifyExecQueued(result.execId)
    }

    private fun request(req: ApiAdhocInvokeRequest) =
        httpTemplate
            .post("/v1/flows/{flowId}/adhoc")
            .path("flowId", testFlow.id)
            .body(req)
            .execute()


    private fun verifyExecQueued(execId: ExecId) {
        with(execQueryRepository.find(execId)!!) {
            assertThat(id, equalTo(execId))
            assertThat(correlation, nullValue())
            assertThat(inputs, equalTo(ExecInputs()))
            assertThat(code, equalTo(ExecCode(value = CodeValue("40 + 2"))))
        }
    }
}

