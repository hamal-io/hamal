package io.hamal.backend.instance.web.req

import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedInvokeExecReq
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class GetReqRouteTest : BaseReqRouteTest() {
    @Test
    fun `Gets req`() {
        val request = awaitCompleted(
            adhoc()
        )

        val response = httpTemplate.get("/v1/reqs/${request.reqId.value}").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val result = response.result(SubmittedInvokeExecReq::class)
        assertThat(result.status, equalTo(ReqStatus.Completed))
    }

    @Test
    fun `Tries to get req which does not exist`() {
        val response = httpTemplate.get("/v1/reqs/123456765432").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(HamalError::class)
        assertThat(error.message, equalTo("Req not found"))
    }

}