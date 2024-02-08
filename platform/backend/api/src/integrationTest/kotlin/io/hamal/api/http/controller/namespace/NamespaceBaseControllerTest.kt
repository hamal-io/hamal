package io.hamal.api.http.controller.namespace

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiFlow
import io.hamal.lib.sdk.api.ApiFlowCreateRequest
import io.hamal.lib.sdk.api.ApiFlowCreateRequested
import io.hamal.lib.sdk.api.ApiFlowList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class NamespaceBaseControllerTest : BaseControllerTest() {
    fun createFlow(req: ApiFlowCreateRequest): ApiFlowCreateRequested {
        val response = httpTemplate.post("/v1/groups/{groupId}/flows")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.result(ApiFlowCreateRequested::class)
    }

    fun listFlows(): ApiFlowList {
        val listFlowsResponse = httpTemplate.get("/v1/groups/{groupId}/flows")
            .path("groupId", testGroup.id)
            .execute()

        assertThat(listFlowsResponse.statusCode, equalTo(Ok))
        require(listFlowsResponse is HttpSuccessResponse) { "request was not successful" }
        return listFlowsResponse.result(ApiFlowList::class)
    }

    fun getFlow(flowId: FlowId): ApiFlow {
        val getFlowResponse = httpTemplate.get("/v1/flows/{flowId}")
            .path("flowId", flowId)
            .execute()

        assertThat(getFlowResponse.statusCode, equalTo(Ok))
        require(getFlowResponse is HttpSuccessResponse) { "request was not successful" }
        return getFlowResponse.result(ApiFlow::class)
    }
}