package io.hamal.api.http.controller.flow

import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.domain.vo.FlowType
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiFlow
import io.hamal.lib.sdk.api.ApiFlowCreateReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FlowGetControllerTest : FlowBaseControllerTest() {
    @Test
    fun `Flow does not exists`() {
        val getFlowResponse = httpTemplate.get("/v1/flows/33333333").execute()
        assertThat(getFlowResponse.statusCode, equalTo(NotFound))
        require(getFlowResponse is HttpErrorResponse) { "request was successful" }

        val error = getFlowResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Flow not found"))
    }

    @Test
    fun `Get flow`() {
        val flowId = awaitCompleted(
            createFlow(
                ApiFlowCreateReq(
                    name = FlowName("flow-one"),
                    inputs = FlowInputs(MapType(mutableMapOf("hamal" to StringType("rockz")))),
                    type = FlowType.default
                )
            )
        ).flowId

        val getFlowResponse = httpTemplate.get("/v1/flows/{flowId}")
            .path("flowId", flowId)
            .execute()

        assertThat(getFlowResponse.statusCode, equalTo(Ok))
        require(getFlowResponse is HttpSuccessResponse) { "request was not successful" }

        with(getFlowResponse.result(ApiFlow::class)) {
            assertThat(id, equalTo(flowId))
            assertThat(type, equalTo(FlowType.default))
            assertThat(name, equalTo(FlowName("flow-one")))
            assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
        }
    }
}