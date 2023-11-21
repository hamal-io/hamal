package io.hamal.bridge.http.controller.queue

import io.hamal.bridge.http.controller.BaseControllerTest
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.bridge.BridgeUnitOfWorkList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseQueueControllerTest : BaseControllerTest() {

    fun dequeue(): BridgeUnitOfWorkList {
        val dequeueResponse = httpTemplate.post("/v1/dequeue").execute()
        assertThat(dequeueResponse.statusCode, equalTo(Ok))

        require(dequeueResponse is HttpSuccessResponse) { "request was not successful" }
        return dequeueResponse.result(BridgeUnitOfWorkList::class)
    }

}