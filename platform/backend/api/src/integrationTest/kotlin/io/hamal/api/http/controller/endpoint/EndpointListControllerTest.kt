package io.hamal.api.http.controller.endpoint

import io.hamal.lib.domain._enum.EndpointMethod.Post
import io.hamal.lib.domain._enum.EndpointMethod.Put
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.sdk.api.ApiEndpointList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class EndpointListControllerTest : EndpointBaseControllerTest() {
    @Test
    fun `No endpoints`() {
        val result = listEndpoints()
        assertThat(result.endpoints, empty())
    }

    @Test
    fun `Single endpoint`() {
        val flowId = awaitCompleted(
            createFlow(
                name = FlowName("namespace"),
                groupId = testGroup.id
            )
        ).flowId

        val funcId = awaitCompleted(
            createFunc(
                flowId = flowId,
                name = FuncName("func")
            )
        ).funcId


        val endpointId = awaitCompleted(
            createEndpoint(
                name = EndpointName("endpoint-one"),
                funcId = funcId,
                flowId = flowId,
                method = Post
            )
        ).endpointId

        with(listEndpoints()) {
            assertThat(endpoints, hasSize(1))
            with(endpoints.first()) {
                assertThat(id, equalTo(endpointId))
                assertThat(name, equalTo(EndpointName("endpoint-one")))
                assertThat(func.name, equalTo(FuncName("func")))
            }
        }
    }

    @Test
    fun `Limit endpoints`() {
        awaitCompleted(
            IntRange(0, 20).map {
                val flowId = awaitCompleted(
                    createFlow(
                        name = FlowName("namespace-$it"),
                        groupId = testGroup.id
                    )
                ).flowId

                val funcId = awaitCompleted(
                    createFunc(
                        flowId = flowId,
                        name = FuncName("func-$it")
                    )
                ).funcId

                createEndpoint(
                    flowId = flowId,
                    funcId = funcId,
                    name = EndpointName("endpoint-$it"),
                    method = Put
                )
            }
        )

        val listResponse = httpTemplate.get("/v1/endpoints")
            .parameter("group_ids", testGroup.id)
            .parameter("limit", 12)
            .execute(ApiEndpointList::class)

        assertThat(listResponse.endpoints, hasSize(12))

        listResponse.endpoints.forEachIndexed { idx, endpoint ->
            assertThat(endpoint.name, equalTo(EndpointName("endpoint-${(20 - idx)}")))
            assertThat(endpoint.func.name, equalTo(FuncName("func-${(20 - idx)}")))
        }
    }

    @Test
    fun `Skip and limit endpoints`() {
        val requests = IntRange(0, 99).map {
            val flowId = awaitCompleted(
                createFlow(
                    name = FlowName("namespace-$it"),
                    groupId = testGroup.id
                )
            ).flowId

            val funcId = awaitCompleted(
                createFunc(
                    flowId = flowId,
                    name = FuncName("func-$it")
                )
            ).funcId

            createEndpoint(
                flowId = flowId,
                funcId = funcId,
                name = EndpointName("endpoint-$it"),
                method = Put
            )
        }

        awaitCompleted(requests)
        val fortyNinth = requests[49]

        val listResponse = httpTemplate.get("/v1/endpoints")
            .parameter("group_ids", testGroup.id)
            .parameter("after_id", fortyNinth.endpointId)
            .parameter("limit", 1)
            .execute(ApiEndpointList::class)

        assertThat(listResponse.endpoints, hasSize(1))

        val endpoint = listResponse.endpoints.first()
        assertThat(endpoint.func.name, equalTo(FuncName("func-48")))
        assertThat(endpoint.name, equalTo(EndpointName("endpoint-48")))
    }
}