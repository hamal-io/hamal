package io.hamal.backend.web.work

import io.hamal.backend.web.BaseRouteTest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.hub.ApiSubmittedReqWithId
import io.hamal.lib.sdk.hub.ApiUnitOfWorkList
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

internal sealed class BaseQueueRouteTest : BaseRouteTest() {
    fun dequeue(): ApiUnitOfWorkList {
        val dequeueResponse = httpTemplate.post("/v1/dequeue").execute()
        MatcherAssert.assertThat(dequeueResponse.statusCode, Matchers.equalTo(HttpStatusCode.Ok))

        require(dequeueResponse is SuccessHttpResponse) { "request was not successful" }
        return dequeueResponse.result(ApiUnitOfWorkList::class)
    }

    fun adhoc(req: InvokeAdhocReq): ApiSubmittedReqWithId =
        httpTemplate
            .post("/v1/adhoc")
            .body(req)
            .execute(ApiSubmittedReqWithId::class)
}