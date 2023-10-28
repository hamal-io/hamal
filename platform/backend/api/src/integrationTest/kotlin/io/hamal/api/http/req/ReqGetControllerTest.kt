//package io.hamal.api.http.req
//
//import io.hamal.lib.domain._enum.ReqStatus.Completed
//import io.hamal.lib.domain.vo.ExecId
//import io.hamal.lib.http.HttpErrorResponse
//import io.hamal.lib.http.HttpStatusCode.Accepted
//import io.hamal.lib.http.HttpStatusCode.NotFound
//import io.hamal.lib.http.HttpSuccessResponse
//import io.hamal.lib.sdk.api.ApiError
//import io.hamal.lib.sdk.toReq
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.equalTo
//import org.junit.jupiter.api.Test
//
//
//internal class ReqGetControllerTest : ReqBaseControllerTest() {
//    @Test
//    fun `Gets req`() {
//        val request = awaitCompleted(
//            adhoc()
//        )
//
//        val response = httpTemplate.get("/v1/reqs/{reqId}").path("reqId", request.reqId).execute()
//        assertThat(response.statusCode, equalTo(Accepted))
//        require(response is HttpSuccessResponse) { "request was not successful" }
//
//        val result = response.toReq<ExecId>()
//        assertThat(result.status, equalTo(Completed))
//    }
//
//    @Test
//    fun `Tries to get req which does not exist`() {
//        val response = httpTemplate.get("/v1/reqs/123456765432").execute()
//        assertThat(response.statusCode, equalTo(NotFound))
//        require(response is HttpErrorResponse) { "request was successful" }
//
//        val error = response.error(ApiError::class)
//        assertThat(error.message, equalTo("Req not found"))
//    }
//
//}