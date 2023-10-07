package io.hamal.admin.web.adhoc

import io.hamal.admin.web.BaseControllerTest
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.admin.AdminInvokeAdhocReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test


internal class AdhocControllerTest : BaseControllerTest() {

    @Test
    fun `Submits adhoc requests without inputs or secrets`() {
        val response = request(
            AdminInvokeAdhocReq(
                inputs = InvocationInputs(),
                code = CodeValue("40 + 2")
            )
        )

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        val result = awaitCompleted(response.result(AdminSubmittedReqWithId::class))
        assertThat(result.status, equalTo(Submitted))

        verifyReqCompleted(result.reqId)
        verifyExecQueued(result.id(::ExecId))
    }


    private fun request(req: AdminInvokeAdhocReq) =
        httpTemplate
            .post("/v1/adhoc")
            .body(req)
            .execute()


    private fun verifyExecQueued(execId: ExecId) {
        with(execQueryRepository.find(execId)!!) {
            assertThat(id, equalTo(execId))
            assertThat(correlation, nullValue())
            assertThat(inputs, equalTo(ExecInputs()))
            assertThat(code, equalTo(CodeValue("40 + 2")))
            assertThat(codeId, nullValue())
            assertThat(codeVersion, nullValue())
        }
    }
}