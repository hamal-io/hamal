package io.hamal.admin.web.req

import io.hamal.lib.domain._enum.ReqStatus.Completed
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.admin.AdminError
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class GetReqControllerTest : BaseReqControllerTest() {
    @Test
    fun `Gets req`() {
        val request = awaitCompleted(
            adhoc()
        )

        val response = httpTemplate.get("/v1/reqs/{reqId}").path("reqId", request.reqId).execute()
        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }

        val result = response.result(AdminSubmittedReqWithId::class)
        assertThat(result.status, equalTo(Completed))
    }

    @Test
    fun `Tries to get req which does not exist`() {
        val response = httpTemplate.get("/v1/reqs/123456765432").execute()
        assertThat(response.statusCode, equalTo(NotFound))
        require(response is ErrorHttpResponse) { "request was successful" }

        val error = response.error(AdminError::class)
        assertThat(error.message, equalTo("Req not found"))
    }

}