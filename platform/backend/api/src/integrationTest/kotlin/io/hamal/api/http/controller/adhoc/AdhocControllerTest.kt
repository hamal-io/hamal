package io.hamal.api.http.controller.adhoc

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiAdhocInvokeReq
import io.hamal.lib.sdk.api.ApiExecInvokeSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test


internal class AdhocControllerTest : BaseControllerTest() {
    @Test
    fun `Submits adhoc requests without inputs or secrets`() {
        val response = request(
            ApiAdhocInvokeReq(
                inputs = InvocationInputs(),
                code = CodeValue("40 + 2")
            )
        )

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }

        val result = awaitCompleted(response.result(ApiExecInvokeSubmitted::class))
        assertThat(result.status, equalTo(Submitted))

        verifyReqCompleted(result.id)
        verifyExecQueued(result.execId)
    }

    private fun request(req: ApiAdhocInvokeReq) =
        httpTemplate
            .post("/v1/namespaces/{namespaceId}/adhoc")
            .path("namespaceId", testNamespace.id)
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

