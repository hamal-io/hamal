package io.hamal.api.http.controller.endpoint

import io.hamal.lib.domain._enum.EndpointMethod.Patch
import io.hamal.lib.domain._enum.EndpointMethod.Post
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.BadRequest
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiEndpointCreateRequest
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class EndpointCreateControllerTest : EndpointBaseControllerTest() {

    @Test
    fun `Create endpoint with default flow id`() {
        val funcId = awaitCompleted(
            createFunc(
                flowId = FlowId(1),
                name = FuncName("func")
            )
        ).funcId

        val result = createEndpoint(
            name = EndpointName("test-endpoint"),
            funcId = funcId,
            method = Patch,
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
        val flowId = awaitCompleted(
            createFlow(
                name = FlowName("flow"),
                groupId = testGroup.id
            )
        ).flowId

        val funcId = awaitCompleted(
            createFunc(
                flowId = flowId,
                name = FuncName("func")
            )
        ).funcId

        val result = createEndpoint(
            name = EndpointName("test-endpoint"),
            funcId = funcId,
            method = Patch,
            flowId = flowId
        )
        awaitCompleted(result)

        with(endpointQueryRepository.get(result.endpointId)) {
            assertThat(name, equalTo(EndpointName("test-endpoint")))
            assertThat(flowId, equalTo(flowId))
        }
    }

    @Test
    fun `Tries to create endpoint, but func does not belong to same flow`() {
        val flowId = awaitCompleted(
            createFlow(
                name = FlowName("flow"),
                groupId = testGroup.id
            )
        ).flowId

        val anotherFlowId = awaitCompleted(
            createFlow(
                name = FlowName("another-flow"),
                groupId = testGroup.id
            )
        ).flowId

        val funcId = awaitCompleted(
            createFunc(
                name = FuncName("func"),
                flowId = anotherFlowId
            )
        ).funcId

        val response = httpTemplate.post("/v1/flows/{flowId}/endpoints")
            .path("flowId", flowId)
            .body(
                ApiEndpointCreateRequest(
                    name = EndpointName("test-endpoint"),
                    funcId = funcId,
                    method = Post
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(BadRequest))
        require(response is HttpErrorResponse) { "request was successful" }

        val error = response.error(ApiError::class)
        assertThat(error.message, equalTo("Endpoint and Func must share the same Flow"))

        assertThat(listEndpoints().endpoints, empty())
    }
}