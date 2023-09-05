package io.hamal.backend.web.adhoc

import io.hamal.backend.web.BaseRouteTest
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test


internal class AdhocRouteTest : BaseRouteTest() {
    @Test
    fun `Submits adhoc requests without inputs or secrets`() {
        val response = request(
            InvokeAdhocReq(
                inputs = InvocationInputs(),
                code = CodeType("40 + 2")
            )
        )

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        val result = awaitCompleted(response.result(HubSubmittedReqWithId::class))
        assertThat(result.status, equalTo(Submitted))

        verifyReqCompleted(result.reqId)
        verifyExecQueued(result.id(::ExecId))
    }


    private fun request(req: InvokeAdhocReq) =
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
            assertThat(code, equalTo(CodeType("40 + 2")))
        }
    }
}