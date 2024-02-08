package io.hamal.api.http.controller.namespace

import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.domain.vo.FlowType
import io.hamal.lib.sdk.api.ApiFlowCreateRequest
import io.hamal.lib.sdk.api.ApiFlowList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class NamespaceListControllerTest : NamespaceBaseControllerTest() {
    @Test
    fun `Only default flows`() {
        val result = listFlows()
        assertThat(result.flows, hasSize(1))

        with(result.flows.first()) {
            assertThat(name, equalTo(FlowName("hamal")))
            assertThat(type, equalTo(FlowType.default))
        }
    }

    @Test
    fun `Single flow`() {
        val flowId = awaitCompleted(
            createFlow(
                ApiFlowCreateRequest(
                    name = FlowName("namespace-one"),
                    inputs = FlowInputs(),
                    type = FlowType.default

                )
            )
        ).flowId

        with(listFlows()) {
            assertThat(flows, hasSize(2))
            with(flows.first()) {
                assertThat(id, equalTo(flowId))
                assertThat(name, equalTo(FlowName("namespace-one")))
                assertThat(type, equalTo(FlowType.default))
            }
        }
    }

    @Test
    fun `Limit flows`() {
        awaitCompleted(
            IntRange(0, 20).map {
                createFlow(
                    ApiFlowCreateRequest(
                        name = FlowName("namespace-$it"),
                        inputs = FlowInputs(),
                        type = FlowType.default
                    )
                )
            }
        )

        val listResponse = httpTemplate.get("/v1/groups/{groupId}/flows")
            .path("groupId", testGroup.id)
            .parameter("limit", 12)
            .execute(ApiFlowList::class)

        assertThat(listResponse.flows, hasSize(12))

        listResponse.flows.forEachIndexed { idx, flow ->
            assertThat(flow.name, equalTo(FlowName("namespace-${(20 - idx)}")))
        }
    }

    @Test
    fun `Skip and limit flows`() {
        val requests = IntRange(0, 99).map {
            createFlow(
                ApiFlowCreateRequest(
                    name = FlowName("namespace-$it"),
                    inputs = FlowInputs(),
                    type = FlowType.default
                )
            )
        }

        awaitCompleted(requests)
        val fortyNinth = requests[49]

        val listResponse = httpTemplate.get("/v1/groups/{groupId}/flows")
            .path("groupId", testGroup.id)
            .parameter("after_id", fortyNinth.flowId)
            .parameter("limit", 1)
            .execute(ApiFlowList::class)

        assertThat(listResponse.flows, hasSize(1))

        val flow = listResponse.flows.first()
        assertThat(flow.name, equalTo(FlowName("namespace-48")))
    }
}