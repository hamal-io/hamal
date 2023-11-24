package io.hamal.api.http.controller.endpoint

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiEndpoint
import io.hamal.lib.sdk.api.ApiEndpointCreateReq
import io.hamal.lib.sdk.api.ApiEndpointCreateSubmitted
import io.hamal.lib.sdk.api.ApiEndpointList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class EndpointBaseControllerTest : BaseControllerTest() {

    fun createEndpoint(
        req: ApiEndpointCreateReq,
        flowId: FlowId = FlowId(1),
    ): ApiEndpointCreateSubmitted {
        val response = httpTemplate.post("/v1/flows/{flowId}/endpoints")
            .path("flowId", flowId)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.result(ApiEndpointCreateSubmitted::class)
    }

    fun listEndpoints(): ApiEndpointList {
        val listEndpointsResponse = httpTemplate.get("/v1/endpoints")
            .parameter("group_ids", testGroup.id)
            .execute()

        assertThat(listEndpointsResponse.statusCode, equalTo(Ok))
        require(listEndpointsResponse is HttpSuccessResponse) { "request was not successful" }
        return listEndpointsResponse.result(ApiEndpointList::class)
    }

    fun getEndpoint(endpointId: EndpointId): ApiEndpoint {
        val getEndpointResponse = httpTemplate.get("/v1/endpoints/{endpointId}")
            .path("endpointId", endpointId)
            .execute()

        assertThat(getEndpointResponse.statusCode, equalTo(Ok))
        require(getEndpointResponse is HttpSuccessResponse) { "request was not successful" }
        return getEndpointResponse.result(ApiEndpoint::class)
    }
}