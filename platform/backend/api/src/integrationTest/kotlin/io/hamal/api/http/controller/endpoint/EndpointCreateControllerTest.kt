package io.hamal.api.http.controller.endpoint

import io.hamal.lib.domain._enum.EndpointMethod.Patch
import io.hamal.lib.domain._enum.EndpointMethod.Post
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
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
    fun `Create endpoint with default namespace id`() {
        val funcId = awaitCompleted(
            createFunc(
                namespaceId = NamespaceId(1),
                name = FuncName("func")
            )
        ).funcId

        val result = createEndpoint(
            name = EndpointName("test-endpoint"),
            funcId = funcId,
            method = Patch,
            namespaceId = NamespaceId(1)
        )
        awaitCompleted(result)

        val endpoint = endpointQueryRepository.get(result.endpointId)
        with(endpoint) {
            assertThat(name, equalTo(EndpointName("test-endpoint")))

            val namespace = namespaceQueryRepository.get(namespaceId)
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
        }

    }


    @Test
    fun `Create endpoint with namespace id`() {
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

        val result = createEndpoint(
            name = EndpointName("test-endpoint"),
            funcId = funcId,
            method = Patch,
            namespaceId = namespaceId
        )
        awaitCompleted(result)

        with(endpointQueryRepository.get(result.endpointId)) {
            assertThat(name, equalTo(EndpointName("test-endpoint")))
            assertThat(namespaceId, equalTo(namespaceId))
        }
    }

    @Test
    fun `Tries to create endpoint, but func does not belong to same namespace`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                name = NamespaceName("namespace"),
                groupId = testGroup.id
            )
        ).namespaceId

        val anotherNamespaceId = awaitCompleted(
            createNamespace(
                name = NamespaceName("another-namespace"),
                groupId = testGroup.id
            )
        ).namespaceId

        val funcId = awaitCompleted(
            createFunc(
                name = FuncName("func"),
                namespaceId = anotherNamespaceId
            )
        ).funcId

        val response = httpTemplate.post("/v1/namespaces/{namespaceId}/endpoints")
            .path("namespaceId", namespaceId)
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
        assertThat(error.message, equalTo("Endpoint and Func must share the same Namespace"))

        assertThat(listEndpoints().endpoints, empty())
    }
}