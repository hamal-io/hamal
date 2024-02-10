package io.hamal.api.http.controller.endpoint

import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiEndpoint
import io.hamal.lib.sdk.api.ApiError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class EndpointGetControllerTest : EndpointBaseControllerTest() {
    @Test
    fun `Endpoint does not exists`() {
        val getEndpointResponse = httpTemplate.get("/v1/endpoints/33333333").execute()
        assertThat(getEndpointResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(getEndpointResponse is HttpErrorResponse) { "request was successful" }

        val error = getEndpointResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Endpoint not found"))
    }

    @Test
    fun `Get endpoint`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                name = NamespaceName("namespace"),
                groupId = testGroup.id
            )
        ).namespaceId

        val funcId = awaitCompleted(
            createFunc(
                namespaceId = namespaceId,
                name = FuncName("func")
            )
        ).funcId

        val endpointId = awaitCompleted(
            createEndpoint(
                namespaceId = namespaceId,
                name = EndpointName("endpoint-one"),
                funcId = funcId,
                method = EndpointMethod.Post
            )
        ).endpointId

        val getEndpointResponse =
            httpTemplate.get("/v1/endpoints/{endpointId}").path("endpointId", endpointId).execute()
        assertThat(getEndpointResponse.statusCode, equalTo(HttpStatusCode.Ok))
        require(getEndpointResponse is HttpSuccessResponse) { "request was not successful" }

        with(getEndpointResponse.result(ApiEndpoint::class)) {
            assertThat(id, equalTo(endpointId))
            assertThat(name, equalTo(EndpointName("endpoint-one")))
        }
    }
}