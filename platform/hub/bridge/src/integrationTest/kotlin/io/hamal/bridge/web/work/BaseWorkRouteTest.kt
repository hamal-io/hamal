package io.hamal.bridge.web.work

import io.hamal.bridge.web.BaseRouteTest
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.sdk.hub.HubUnitOfWorkList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseWorkRouteTest : BaseRouteTest() {

    fun dequeue(): HubUnitOfWorkList {
        val dequeueResponse = httpTemplate.post("/v1/dequeue").execute()
        assertThat(dequeueResponse.statusCode, equalTo(Ok))

        require(dequeueResponse is SuccessHttpResponse) { "request was not successful" }
        return dequeueResponse.result(HubUnitOfWorkList::class)
    }

}