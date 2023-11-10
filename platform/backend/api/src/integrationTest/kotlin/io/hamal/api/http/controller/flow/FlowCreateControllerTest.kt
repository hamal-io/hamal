package io.hamal.api.http.controller.flow

import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiFlowCreateReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FlowCreateControllerTest : FlowBaseControllerTest() {
    @Test
    fun `Create flow`() {
        val result = createFlow(
            ApiFlowCreateReq(
                name = FlowName("test-flow"),
                inputs = FlowInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))
            )
        )
        awaitCompleted(result)
        verifyFlowCreated(result.flowId)
    }
}

private fun FlowCreateControllerTest.verifyFlowCreated(flowId: FlowId) {
    with(flowQueryRepository.get(flowId)) {
        assertThat(id, equalTo(flowId))
        assertThat(name, equalTo(FlowName("test-flow")))
        assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
    }
}