package io.hamal.backend.instance.web.queue

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.DequeueExecsResponse
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

internal sealed class BaseQueueRouteTest : BaseRouteTest() {
    fun dequeue(): DequeueExecsResponse {
        val dequeueResponse = httpTemplate.post("/v1/dequeue").execute()
        MatcherAssert.assertThat(dequeueResponse.statusCode, Matchers.equalTo(HttpStatusCode.Ok))

        require(dequeueResponse is SuccessHttpResponse) { "request was not successful" }
        return dequeueResponse.result(DequeueExecsResponse::class)
    }

    fun adhoc(req: InvokeAdhocReq): SubmittedInvokeAdhocReq =
        httpTemplate
            .post("/v1/adhoc")
            .body(req)
            .execute(SubmittedInvokeAdhocReq::class)

}