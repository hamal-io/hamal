package io.hamal.admin.web.namespace

import io.hamal.admin.web.BaseControllerTest
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.admin.AdminCreateNamespaceReq
import io.hamal.lib.sdk.admin.AdminNamespace
import io.hamal.lib.sdk.admin.AdminNamespaceList
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseNamespaceControllerTest : BaseControllerTest() {
    fun createNamespace(req: AdminCreateNamespaceReq): AdminSubmittedReqWithId {
        val response = httpTemplate.post("/v1/groups/{groupId}/namespaces")
            .path("groupId", testGroup.id)
            .body(req)
            .execute()

        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(AdminSubmittedReqWithId::class)
    }

    fun listNamespaces(): AdminNamespaceList {
        val listNamespacesResponse = httpTemplate.get("/v1/groups/{groupId}/namespaces")
            .path("groupId", testGroup.id)
            .execute()

        assertThat(listNamespacesResponse.statusCode, equalTo(Ok))
        require(listNamespacesResponse is SuccessHttpResponse) { "request was not successful" }
        return listNamespacesResponse.result(AdminNamespaceList::class)
    }

    fun getNamespace(namespaceId: NamespaceId): AdminNamespace {
        val getNamespaceResponse = httpTemplate.get("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespaceId)
            .execute()

        assertThat(getNamespaceResponse.statusCode, equalTo(Ok))
        require(getNamespaceResponse is SuccessHttpResponse) { "request was not successful" }
        return getNamespaceResponse.result(AdminNamespace::class)
    }
}