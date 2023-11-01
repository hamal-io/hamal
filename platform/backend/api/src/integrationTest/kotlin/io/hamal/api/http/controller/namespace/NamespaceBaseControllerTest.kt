package io.hamal.api.http.controller.namespace

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiNamespace
import io.hamal.lib.sdk.api.ApiNamespaceCreateReq
import io.hamal.lib.sdk.api.ApiNamespaceCreateSubmitted
import io.hamal.lib.sdk.api.ApiNamespaceList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class NamespaceBaseControllerTest : BaseControllerTest() {
    fun createNamespace(req: ApiNamespaceCreateReq): ApiNamespaceCreateSubmitted {
        val response = httpTemplate.post("/v1/groups/{groupId}/namespaces")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is HttpSuccessResponse) { "request was not successful" }
        return response.result(ApiNamespaceCreateSubmitted::class)
    }

    fun listNamespaces(): ApiNamespaceList {
        val listNamespacesResponse = httpTemplate.get("/v1/groups/{groupId}/namespaces")
            .path("groupId", testGroup.id)
            .execute()

        assertThat(listNamespacesResponse.statusCode, equalTo(Ok))
        require(listNamespacesResponse is HttpSuccessResponse) { "request was not successful" }
        return listNamespacesResponse.result(ApiNamespaceList::class)
    }

    fun getNamespace(namespaceId: NamespaceId): ApiNamespace {
        val getNamespaceResponse = httpTemplate.get("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespaceId)
            .execute()

        assertThat(getNamespaceResponse.statusCode, equalTo(Ok))
        require(getNamespaceResponse is HttpSuccessResponse) { "request was not successful" }
        return getNamespaceResponse.result(ApiNamespace::class)
    }
}