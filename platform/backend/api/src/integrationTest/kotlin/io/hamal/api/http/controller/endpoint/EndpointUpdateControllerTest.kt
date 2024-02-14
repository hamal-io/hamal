package io.hamal.api.http.controller.endpoint

import io.hamal.lib.domain._enum.EndpointMethod.Delete
import io.hamal.lib.domain._enum.EndpointMethod.Put
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.BadRequest
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiEndpointUpdateRequested
import io.hamal.lib.sdk.api.ApiEndpointUpdateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class EndpointUpdateControllerTest : EndpointBaseControllerTest() {

    @Test
    fun `Updates endpoint`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                name = NamespaceName("namespace"),
                workspaceId = testWorkspace.id
            )
        ).namespaceId

        val funcId = awaitCompleted(
            createFunc(
                namespaceId = namespaceId,
                name = FuncName("func")
            )
        ).funcId

        val anotherFuncId = awaitCompleted(
            createFunc(
                namespaceId = namespaceId,
                name = FuncName("another-func")
            )
        ).funcId

        val endpoint = awaitCompleted(
            createEndpoint(
                namespaceId = namespaceId,
                funcId = funcId,
                name = EndpointName("created-name"),
                method = Delete
            )
        )

        val updateEndpointResponse = httpTemplate.patch("/v1/endpoints/{endpointId}")
            .path("endpointId", endpoint.endpointId)
            .body(
                ApiEndpointUpdateRequest(
                    funcId = anotherFuncId,
                    name = EndpointName("updated-name"),
                    method = Put
                )
            )
            .execute()

        assertThat(updateEndpointResponse.statusCode, equalTo(Accepted))
        require(updateEndpointResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateEndpointResponse.result(ApiEndpointUpdateRequested::class)
        awaitCompleted(submittedReq)
        with(getEndpoint(submittedReq.endpointId)) {
            assertThat(id, equalTo(submittedReq.endpointId))
            assertThat(func.name, equalTo(FuncName("another-func")))
            assertThat(name, equalTo(EndpointName("updated-name")))
        }
    }

    @Test
    fun `Updates endpoint without updating values`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                name = NamespaceName("namespace"),
                workspaceId = testWorkspace.id
            )
        ).namespaceId

        val funcId = awaitCompleted(
            createFunc(
                namespaceId = namespaceId,
                name = FuncName("func")
            )
        ).funcId

        val endpoint = awaitCompleted(
            createEndpoint(
                namespaceId = namespaceId,
                funcId = funcId,
                name = EndpointName("created-name"),
                method = Delete
            )
        )

        val updateEndpointResponse = httpTemplate.patch("/v1/endpoints/{endpointId}")
            .path("endpointId", endpoint.endpointId)
            .body(
                ApiEndpointUpdateRequest(
                    funcId = null,
                    name = null,
                    method = null
                )
            )
            .execute()

        assertThat(updateEndpointResponse.statusCode, equalTo(Accepted))
        require(updateEndpointResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateEndpointResponse.result(ApiEndpointUpdateRequested::class)
        awaitCompleted(submittedReq)
        with(getEndpoint(submittedReq.endpointId)) {
            assertThat(id, equalTo(submittedReq.endpointId))
            assertThat(func.name, equalTo(FuncName("func")))
        }
    }


    @Test
    fun `Tries to set func which does not belong to the same namespace`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                name = NamespaceName("namespace"),
                workspaceId = testWorkspace.id
            )
        ).namespaceId

        val anotherNamespaceId = awaitCompleted(
            createNamespace(
                name = NamespaceName("another-namespace"),
                workspaceId = testWorkspace.id
            )
        ).namespaceId

        val funcId = awaitCompleted(
            createFunc(
                namespaceId = namespaceId,
                name = FuncName("func")
            )
        ).funcId

        val anotherFuncId = awaitCompleted(
            createFunc(
                namespaceId = anotherNamespaceId,
                name = FuncName("another-func")
            )
        ).funcId

        val endpoint = awaitCompleted(
            createEndpoint(
                namespaceId = namespaceId,
                funcId = funcId,
                name = EndpointName("created-name"),
                method = Delete
            )
        )

        val updateEndpointResponse = httpTemplate.patch("/v1/endpoints/{endpointId}")
            .path("endpointId", endpoint.endpointId)
            .body(
                ApiEndpointUpdateRequest(
                    funcId = anotherFuncId,
                    name = EndpointName("updated-name"),
                    method = Put
                )
            )
            .execute()

        assertThat(updateEndpointResponse.statusCode, equalTo(BadRequest))
        require(updateEndpointResponse is HttpErrorResponse) { "request was not successful" }

        with(getEndpoint(endpoint.endpointId)) {
            assertThat(id, equalTo(endpoint.endpointId))
            assertThat(name, equalTo(EndpointName("created-name")))
            assertThat(func.name, equalTo(FuncName("func")))
        }
    }
}