package io.hamal.api.http.adhoc

import io.hamal.api.http.BaseControllerTest
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiInvokeAdhocReq
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test


internal class AdhocControllerTest : BaseControllerTest() {
    @Test
    fun `Submits adhoc requests without inputs or secrets`() {
        val response = request(
            ApiInvokeAdhocReq(
                inputs = InvocationInputs(),
                code = CodeValue("40 + 2")
            )
        )

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        val result = awaitCompleted(response.result(ApiSubmittedReqWithId::class))
        assertThat(result.status, equalTo(Submitted))

        verifyReqCompleted(result.reqId)
        verifyExecQueued(result.id(::ExecId))
    }


    private fun request(req: ApiInvokeAdhocReq) =
        httpTemplate
            .post("/v1/groups/{groupId}/adhoc")
            .path("groupId", testGroup.id)
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