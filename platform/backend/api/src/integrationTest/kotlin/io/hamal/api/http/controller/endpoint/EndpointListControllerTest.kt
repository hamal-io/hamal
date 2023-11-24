package io.hamal.api.http.controller.endpoint

import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.sdk.api.ApiEndpointCreateReq
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
        val endpointId = awaitCompleted(
            createEndpoint(ApiEndpointCreateReq(EndpointName("endpoint-one")))
        ).endpointId

        with(listEndpoints()) {
            assertThat(endpoints, hasSize(1))
            with(endpoints.first()) {
                assertThat(id, equalTo(endpointId))
                assertThat(name, equalTo(EndpointName("endpoint-one")))
            }
        }
    }

    @Test
    fun `Limit endpoints`() {
        awaitCompleted(
            IntRange(0, 20).map {
                createEndpoint(ApiEndpointCreateReq(EndpointName("endpoint-$it")))
            }
        )

        val listResponse = httpTemplate.get("/v1/endpoints")
            .parameter("group_ids", testGroup.id)
            .parameter("limit", 12)
            .execute(ApiEndpointList::class)

        assertThat(listResponse.endpoints, hasSize(12))

        listResponse.endpoints.forEachIndexed { idx, endpoint ->
            assertThat(endpoint.name, equalTo(EndpointName("endpoint-${(20 - idx)}")))
        }
    }

    @Test
    fun `Skip and limit endpoints`() {
        val requests = IntRange(0, 99).map {
            createEndpoint(ApiEndpointCreateReq(EndpointName("endpoint-$it")))
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
        assertThat(endpoint.flow.name, equalTo(FlowName("hamal")))
        assertThat(endpoint.name, equalTo(EndpointName("endpoint-48")))
    }
}