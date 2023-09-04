package io.hamal.backend.web.work

import io.hamal.backend.web.BaseRouteTest
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import io.hamal.lib.sdk.hub.HubUnitOfWorkList
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

internal sealed class BaseQueueRouteTest : BaseRouteTest() {
    fun dequeue(): HubUnitOfWorkList {
        val dequeueResponse = httpTemplate.post("/v1/dequeue").execute()
        MatcherAssert.assertThat(dequeueResponse.statusCode, Matchers.equalTo(HttpStatusCode.Ok))

        require(dequeueResponse is SuccessHttpResponse) { "request was not successful" }
        return dequeueResponse.result(HubUnitOfWorkList::class)
    }

    fun adhoc(req: InvokeAdhocReq): HubSubmittedReqWithId =
        httpTemplate
            .post("/v1/adhoc")
            .body(req)
            .execute(HubSubmittedReqWithId::class)
}