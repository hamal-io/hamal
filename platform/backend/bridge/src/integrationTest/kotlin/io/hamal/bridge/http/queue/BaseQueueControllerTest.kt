package io.hamal.bridge.http.queue

import io.hamal.bridge.http.BaseControllerTest
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiUnitOfWorkList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseQueueControllerTest : BaseControllerTest() {

    fun dequeue(): ApiUnitOfWorkList {
        val dequeueResponse = httpTemplate.post("/v1/dequeue").execute()
        assertThat(dequeueResponse.statusCode, equalTo(Ok))

        require(dequeueResponse is HttpSuccessResponse) { "request was not successful" }
        return dequeueResponse.result(ApiUnitOfWorkList::class)
    }

}