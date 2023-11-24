package io.hamal.api.http.controller.endpoint

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiEndpointCreateReq
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class EndpointCreateControllerTest : EndpointBaseControllerTest() {

    @Test
    fun `Create endpoint with default flow id`() {
        val result = createEndpoint(
            req = ApiEndpointCreateReq(
                name = EndpointName("test-endpoint"),
            ),
            flowId = FlowId(1)
        )
        awaitCompleted(result)

        val endpoint = endpointQueryRepository.get(result.endpointId)
        with(endpoint) {
            assertThat(name, equalTo(EndpointName("test-endpoint")))

            val flow = flowQueryRepository.get(flowId)
            assertThat(flow.name, equalTo(FlowName("hamal")))
        }

    }


    @Test
    fun `Create endpoint with flow id`() {
        val flow = flowCmdRepository.create(
            CreateCmd(
                id = CmdId(1),
                flowId = FlowId(2345),
                groupId = testGroup.id,
                name = FlowName("hamal::name::space"),
                inputs = FlowInputs()
            )
        )

        val result = createEndpoint(
            req = ApiEndpointCreateReq(EndpointName("test-endpoint")),
            flowId = flow.id
        )
        awaitCompleted(result)

        with(endpointQueryRepository.get(result.endpointId)) {
            assertThat(name, equalTo(EndpointName("test-endpoint")))

            flowQueryRepository.get(flowId).let {
                assertThat(it.id, equalTo(flow.id))
                assertThat(it.name, equalTo(FlowName("hamal::name::space")))
            }
        }
    }

    @Test
    fun `Tries to create endpoint with flow which does not exist`() {

        val response = httpTemplate.post("/v1/flows/12345/endpoints")
            .path("groupId", testGroup.id)
            .body(ApiEndpointCreateReq(EndpointName("test-endpoint")))
            .execute()

        assertThat(response.statusCode, equalTo(NotFound))
        require(response is HttpErrorResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Flow not found"))

        assertThat(listEndpoints().endpoints, empty())
    }
}