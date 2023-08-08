package io.hamal.backend.instance.web.adhoc

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedInvokeExecReq
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.value.CodeValue
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
                code = CodeValue("40 + 2")
            )
        )

        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        val result = response.result(SubmittedInvokeExecReq::class)

        assertThat(result.status, equalTo(ReqStatus.Submitted))
        assertThat(result.inputs, equalTo(InvocationInputs()))
        assertThat(result.code, equalTo(CodeValue("40 + 2")))

        Thread.sleep(10)

        verifyReqCompleted(result.reqId)
        verifyExecQueued(result.id)
    }


    private fun request(req: InvokeAdhocReq) =
        httpTemplate
            .post("/v1/adhoc")
            .body(req)
            .execute()


    private fun verifyExecQueued(execId: ExecId) {
        with(execQueryRepository.find(execId)!!) {
            assertThat(id, equalTo(execId))
            assertThat(status, equalTo(ExecStatus.Queued))

            assertThat(correlation, nullValue())
            assertThat(inputs, equalTo(ExecInputs()))
            assertThat(code, equalTo(CodeValue("40 + 2")))
        }
    }
}