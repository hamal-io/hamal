package io.hamal.api.http.controller.flow

import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.domain.vo.FlowType
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFlowCreateReq
import io.hamal.lib.sdk.api.ApiFlowUpdateReq
import io.hamal.lib.sdk.api.ApiFlowUpdateSubmitted
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FlowUpdateControllerTest : FlowBaseControllerTest() {

    @Test
    fun `Tries to update flow which does not exists`() {
        val getFlowResponse = httpTemplate.patch("/v1/flows/33333333")
            .body(
                ApiFlowUpdateReq(
                    name = FlowName("update"),
                    inputs = FlowInputs(),
                )
            )
            .execute()

        assertThat(getFlowResponse.statusCode, equalTo(NotFound))
        require(getFlowResponse is HttpErrorResponse) { "request was successful" }

        val error = getFlowResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Flow not found"))
    }

    @Test
    fun `Updates flow`() {
        val flow = awaitCompleted(
            createFlow(
                ApiFlowCreateReq(
                    name = FlowName("createdName"),
                    inputs = FlowInputs(MapType((mutableMapOf("hamal" to StringType("createdInputs"))))),
                    type = FlowType.default
                )
            )
        )

        val updateFlowResponse = httpTemplate.patch("/v1/flows/{flowId}")
            .path("flowId", flow.flowId)
            .body(
                ApiFlowUpdateReq(
                    name = FlowName("updatedName"),
                    inputs = FlowInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs"))))
                )
            )
            .execute()
        assertThat(updateFlowResponse.statusCode, equalTo(Accepted))
        require(updateFlowResponse is HttpSuccessResponse) { "request was not successful" }

        val req = updateFlowResponse.result(ApiFlowUpdateSubmitted::class)
        val flowId = awaitCompleted(req).flowId

        with(getFlow(flowId)) {
            assertThat(id, equalTo(flowId))
            assertThat(name, equalTo(FlowName("updatedName")))
            assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs"))))))
            assertThat(type, equalTo(FlowType.default))
        }
    }
}