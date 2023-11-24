package io.hamal.api.http.controller.endpoint

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiEndpointCreateReq
import io.hamal.lib.sdk.api.ApiEndpointUpdateSubmitted
import io.hamal.lib.sdk.api.ApiUpdateEndpointReq
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class EndpointUpdateControllerTest : EndpointBaseControllerTest() {

    @Test
    fun `Tries to update endpoint which does not exists`() {
        val getEndpointResponse = httpTemplate.patch("/v1/endpoints/33333333")
            .body(ApiUpdateEndpointReq(name = EndpointName("update")))
            .execute()

        assertThat(getEndpointResponse.statusCode, equalTo(NotFound))
        require(getEndpointResponse is HttpErrorResponse) { "request was successful" }

        val error = getEndpointResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Endpoint not found"))
    }

    @Test
    fun `Updates endpoint`() {
        val createdFlow = flowCmdRepository.create(
            CreateCmd(
                id = CmdId(2),
                flowId = FlowId(2),
                groupId = testGroup.id,
                name = FlowName("createdFlow"),
                inputs = FlowInputs()
            )
        )

        val endpoint = awaitCompleted(
            createEndpoint(
                req = ApiEndpointCreateReq(EndpointName("createdName")),
                flowId = createdFlow.id
            )
        )

        val updateEndpointResponse = httpTemplate.patch("/v1/endpoints/{endpointId}")
            .path("endpointId", endpoint.endpointId)
            .body(ApiUpdateEndpointReq(name = EndpointName("updatedName")))
            .execute()

        assertThat(updateEndpointResponse.statusCode, equalTo(Accepted))
        require(updateEndpointResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateEndpointResponse.result(ApiEndpointUpdateSubmitted::class)
        awaitCompleted(submittedReq)
        with(getEndpoint(submittedReq.endpointId)) {
            assertThat(id, equalTo(submittedReq.endpointId))
            assertThat(flow.name, equalTo(FlowName("createdFlow")))
            assertThat(name, equalTo(EndpointName("updatedName")))
        }
    }

    @Test
    fun `Updates endpoint without updating values`() {
        val createdFlow = flowCmdRepository.create(
            CreateCmd(
                id = CmdId(2),
                flowId = FlowId(2),
                groupId = testGroup.id,
                name = FlowName("createdFlow"),
                inputs = FlowInputs()
            )
        )

        val endpoint = awaitCompleted(
            createEndpoint(
                req = ApiEndpointCreateReq(EndpointName("createdName")),
                flowId = createdFlow.id
            )
        )

        val updateEndpointResponse = httpTemplate.patch("/v1/endpoints/{endpointId}")
            .path("endpointId", endpoint.endpointId)
            .body(ApiUpdateEndpointReq(name = null))
            .execute()
        assertThat(updateEndpointResponse.statusCode, equalTo(Accepted))
        require(updateEndpointResponse is HttpSuccessResponse) { "request was not successful" }

        val req = updateEndpointResponse.result(ApiEndpointUpdateSubmitted::class)
        awaitCompleted(req)

        with(getEndpoint(req.endpointId)) {
            assertThat(id, equalTo(req.endpointId))
            assertThat(flow.name, equalTo(FlowName("createdFlow")))
            assertThat(name, equalTo(EndpointName("createdName")))
        }
    }
}