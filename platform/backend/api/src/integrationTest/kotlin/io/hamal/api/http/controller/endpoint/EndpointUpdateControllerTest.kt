package io.hamal.api.http.controller.endpoint

import io.hamal.lib.domain._enum.EndpointMethod.Delete
import io.hamal.lib.domain._enum.EndpointMethod.Put
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.BadRequest
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiEndpointUpdateRequest
import io.hamal.lib.sdk.api.ApiEndpointUpdateRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class EndpointUpdateControllerTest : EndpointBaseControllerTest() {

    @Test
    fun `Updates endpoint`() {
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

        val anotherFuncId = awaitCompleted(
            createFunc(
                namespaceId = namespaceId,
                name = FuncName("another-func")
            )
        ).id

        val endpoint = awaitCompleted(
            createEndpoint(
                namespaceId = namespaceId,
                funcId = funcId,
                name = EndpointName("created-name"),
                method = Delete
            )
        )

        val updateEndpointResponse = httpTemplate.patch("/v1/endpoints/{endpointId}")
            .path("endpointId", endpoint.id)
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
        with(getEndpoint(submittedReq.id)) {
            assertThat(id, equalTo(submittedReq.id))
            assertThat(func.name, equalTo(FuncName("another-func")))
            assertThat(name, equalTo(EndpointName("updated-name")))
        }
    }

    @Test
    fun `Updates endpoint without updating values`() {
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

        val endpoint = awaitCompleted(
            createEndpoint(
                namespaceId = namespaceId,
                funcId = funcId,
                name = EndpointName("created-name"),
                method = Delete
            )
        )

        val updateEndpointResponse = httpTemplate.patch("/v1/endpoints/{endpointId}")
            .path("endpointId", endpoint.id)
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
        with(getEndpoint(submittedReq.id)) {
            assertThat(id, equalTo(submittedReq.id))
            assertThat(func.name, equalTo(FuncName("func")))
        }
    }


    @Test
    fun `Tries to set func which does not belong to the same namespace`() {
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
                namespaceId = namespaceId,
                name = FuncName("func")
            )
        ).id

        val anotherFuncId = awaitCompleted(
            createFunc(
                namespaceId = anotherNamespaceId,
                name = FuncName("another-func")
            )
        ).id

        val endpoint = awaitCompleted(
            createEndpoint(
                namespaceId = namespaceId,
                funcId = funcId,
                name = EndpointName("created-name"),
                method = Delete
            )
        )

        val updateEndpointResponse = httpTemplate.patch("/v1/endpoints/{endpointId}")
            .path("endpointId", endpoint.id)
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

        with(getEndpoint(endpoint.id)) {
            assertThat(id, equalTo(endpoint.id))
            assertThat(name, equalTo(EndpointName("created-name")))
            assertThat(func.name, equalTo(FuncName("func")))
        }
    }
}