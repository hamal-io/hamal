package io.hamal.backend.instance.web.req

import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class GetReqRouteIT : BaseReqRouteIT() {
    @Test
    fun `Gets req`() {
        val request = adhoc().also { awaitReqCompleted(it.id) }

        val response = httpTemplate.get("/v1/reqs/${request.id.value}").execute()
        assertThat(response.statusCode, equalTo(HttpStatusCode.Ok))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val result = response.result(SubmittedInvokeAdhocReq::class)
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