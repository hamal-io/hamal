package io.hamal.api.http.controller.endpoint

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class EndpointBaseControllerTest : BaseControllerTest() {

    fun createEndpoint(
        funcId: FuncId,
        name: EndpointName,
        method: EndpointMethod,
        namespaceId: NamespaceId = NamespaceId(1),
    ): ApiEndpointCreateRequested {
        val response = httpTemplate.post("/v1/namespaces/{namespaceId}/endpoints")
            .path("namespaceId", namespaceId)
            .body(
                ApiEndpointCreateRequest(
                    name = name,
                    funcId = funcId,
                    method = method
                )
            )
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.result(ApiEndpointCreateRequested::class)
    }

    fun createFunc(
        name: FuncName,
        namespaceId: NamespaceId = NamespaceId.root
    ): ApiFuncCreateRequested {
        val createTopicResponse = httpTemplate.post("/v1/namespaces/{namespaceId}/funcs")
            .path("namespaceId", namespaceId)
            .body(
                ApiFuncCreateRequest(
                    name = name,
                    inputs = FuncInputs(),
                    code = CodeValue("")
                )
            )
            .execute()

        assertThat(createTopicResponse.statusCode, equalTo(Accepted))
        require(createTopicResponse is HttpSuccessResponse) { "request was not successful" }

        return createTopicResponse.result(ApiFuncCreateRequested::class)
    }

    fun appendNamespace(
        name: NamespaceName,
        parentId: NamespaceId,
    ): ApiNamespaceAppendRequested {
        val response = httpTemplate.post("/v1/namespaces/{namespaceId}/namespaces")
            .path("namespaceId", parentId)
            .body(ApiNamespaceAppendRequest(name))
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.result(ApiNamespaceAppendRequested::class)
    }


    fun listEndpoints(): ApiEndpointList {
        val listEndpointsResponse = httpTemplate.get("/v1/endpoints")
            .parameter("workspace_ids", testWorkspace.id)
            .execute()

        assertThat(listEndpointsResponse.statusCode, equalTo(Ok))
        require(listEndpointsResponse is HttpSuccessResponse) { "request was not successful" }
        return listEndpointsResponse.result(ApiEndpointList::class)
    }

    fun getEndpoint(endpointId: EndpointId): ApiEndpoint {
        val getEndpointResponse = httpTemplate.get("/v1/endpoints/{endpointId}")
            .path("endpointId", endpointId)
            .execute()

        assertThat(getEndpointResponse.statusCode, equalTo(Ok))
        require(getEndpointResponse is HttpSuccessResponse) { "request was not successful" }
        return getEndpointResponse.result(ApiEndpoint::class)
    }
}