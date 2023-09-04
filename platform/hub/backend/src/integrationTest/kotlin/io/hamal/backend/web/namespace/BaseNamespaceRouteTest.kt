package io.hamal.backend.web.namespace

import io.hamal.backend.web.BaseRouteTest
import io.hamal.lib.domain.req.CreateNamespaceReq
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.hub.HubNamespace
import io.hamal.lib.sdk.hub.HubNamespaceList
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseNamespaceRouteTest : BaseRouteTest() {
    fun createNamespace(req: CreateNamespaceReq): HubSubmittedReqWithId {
        val response = httpTemplate.post("/v1/namespaces").body(req).execute()
        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(HubSubmittedReqWithId::class)
    }

    fun listNamespaces(): HubNamespaceList {
        val listNamespacesResponse = httpTemplate.get("/v1/namespaces").execute()
        assertThat(listNamespacesResponse.statusCode, equalTo(Ok))
        require(listNamespacesResponse is SuccessHttpResponse) { "request was not successful" }
        return listNamespacesResponse.result(HubNamespaceList::class)
    }

    fun getNamespace(namespaceId: NamespaceId): HubNamespace {
        val getNamespaceResponse = httpTemplate.get("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespaceId)
            .execute()
        assertThat(getNamespaceResponse.statusCode, equalTo(Ok))
        require(getNamespaceResponse is SuccessHttpResponse) { "request was not successful" }
        return getNamespaceResponse.result(HubNamespace::class)
    }
}