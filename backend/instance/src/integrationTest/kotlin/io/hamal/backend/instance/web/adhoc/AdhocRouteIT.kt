package io.hamal.backend.instance.web.adhoc

import io.hamal.backend.instance.web.BaseRouteIT
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test


internal class AdhocRouteIT : BaseRouteIT() {
    @Test
    fun `Submits adhoc requests without inputs or secrets`() {
        val response = request(
            InvokeAdhocReq(
                inputs = InvocationInputs(),
                secrets = InvocationSecrets(),
                code = Code("40 + 2")
            )
        )

        assertThat(response.statusCode, equalTo(HttpStatusCode.Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        val result = response.result(SubmittedInvokeAdhocReq::class)

        assertThat(result.status, equalTo(ReqStatus.Submitted))
        assertThat(result.inputs, equalTo(InvocationInputs()))
        assertThat(result.secrets, equalTo(InvocationSecrets()))
        assertThat(result.code, equalTo(Code("40 + 2")))

        Thread.sleep(10)

        verifyReqCompleted(result.id)
        verifyExecQueued(result.execId)
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
            assertThat(secrets, equalTo(ExecSecrets()))
            assertThat(code, equalTo(Code("40 + 2")))
        }
    }
}