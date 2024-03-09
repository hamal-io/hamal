package io.hamal.api.http.controller.endpoint

import io.hamal.lib.domain._enum.EndpointMethod.Patch
import io.hamal.lib.domain._enum.EndpointMethod.Post
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
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
                namespaceId = NamespaceId.root,
                name = FuncName("func")
            )
        ).id

        val result = createEndpoint(
            name = EndpointName("test-endpoint"),
            funcId = funcId,
            method = Patch,
            namespaceId = NamespaceId.root
        )
        awaitCompleted(result)

        val endpoint = endpointQueryRepository.get(result.id)
        with(endpoint) {
            assertThat(name, equalTo(EndpointName("test-endpoint")))

            val namespace = namespaceQueryRepository.get(namespaceId)
            assertThat(namespace.name, equalTo(NamespaceName("hamal")))
        }

    }


    @Test
    fun `Create endpoint with namespace id`() {
        val namespaceId = awaitCompleted(
            appendNamespace(
                name = NamespaceName("namespace"),
                parentId = testNamespace.id
            )
        ).id

        val funcId = awaitCompleted(
            createFunc(
                namespaceId = namespaceId,
                name = FuncName("func")
            )
        ).id

        val result = createEndpoint(
            name = EndpointName("test-endpoint"),
            funcId = funcId,
            method = Patch,
            namespaceId = namespaceId
        )
        awaitCompleted(result)

        with(endpointQueryRepository.get(result.id)) {
            assertThat(name, equalTo(EndpointName("test-endpoint")))
            assertThat(namespaceId, equalTo(namespaceId))
        }
    }

    @Test
    fun `Tries to create endpoint, but func does not belong to same namespace`() {
        val namespaceId = awaitCompleted(
            appendNamespace(
                name = NamespaceName("namespace"),
                parentId = testNamespace.id
            )
        ).id

        val anotherNamespaceId = awaitCompleted(
            appendNamespace(
                name = NamespaceName("another-namespace"),
                parentId = testNamespace.id
            )
        ).id

        val funcId = awaitCompleted(
            createFunc(
                name = FuncName("func"),
                namespaceId = anotherNamespaceId
            )
        ).id

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