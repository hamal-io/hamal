package io.hamal.api.http.controller.flow

import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.domain.vo.FlowType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiFlowCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FlowCreateControllerTest : FlowBaseControllerTest() {
    @Test
    fun `Create flow`() {
        val flowId = awaitCompleted(
            createFlow(
                ApiFlowCreateRequest(
                    name = FlowName("test-flow"),
                    inputs = FlowInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                    type = null
                )
            )
        ).flowId

        with(flowQueryRepository.get(flowId)) {
            assertThat(id, equalTo(flowId))
            assertThat(type, equalTo(FlowType.default))
            assertThat(name, equalTo(FlowName("test-flow")))
            assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
        }
    }

    @Test
    fun `Create flow with type`() {
        val flowId = awaitCompleted(
            createFlow(
                ApiFlowCreateRequest(
                    name = FlowName("test-flow"),
                    inputs = FlowInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                    type = FlowType("SpecialFlowType")
                )
            )
        ).flowId

        with(flowQueryRepository.get(flowId)) {
            assertThat(id, equalTo(flowId))
            assertThat(type, equalTo(FlowType("SpecialFlowType")))
            assertThat(name, equalTo(FlowName("test-flow")))
            assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
        }
    }
}