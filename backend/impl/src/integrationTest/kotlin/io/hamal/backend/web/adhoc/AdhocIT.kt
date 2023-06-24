package io.hamal.backend.web.adhoc

import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.web.BaseIT
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort


class AdhocIT(
    @LocalServerPort localPort: Int,
    @Autowired reqQueryRepository: ReqQueryRepository,
    @Autowired eventBrokerRepository: LogBrokerRepository<*>,
    @Autowired val execQueryRepository: ExecQueryRepository
) : BaseIT(
    localPort = localPort,
    reqQueryRepository = reqQueryRepository,
    eventBrokerRepository = eventBrokerRepository
) {
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

        Thread.sleep(100)

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