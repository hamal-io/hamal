package io.hamal.backend.instance.web.namespace

import io.hamal.backend.instance.web.BaseRouteTest
import io.hamal.lib.domain.req.CreateNamespaceReq
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.ApiNamespace
import io.hamal.lib.sdk.domain.ApiNamespaceList
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseNamespaceRouteTest : BaseRouteTest() {
    fun createNamespace(req: CreateNamespaceReq): ApiSubmittedReqWithId {
        val response = httpTemplate.post("/v1/namespaces").body(req).execute()
        assertThat(response.statusCode, equalTo(Accepted))
        require(response is SuccessHttpResponse) { "request was not successful" }
        return response.result(ApiSubmittedReqWithId::class)
    }

    fun listNamespaces(): ApiNamespaceList {
        val listNamespacesResponse = httpTemplate.get("/v1/namespaces").execute()
        assertThat(listNamespacesResponse.statusCode, equalTo(Ok))
        require(listNamespacesResponse is SuccessHttpResponse) { "request was not successful" }
        return listNamespacesResponse.result(ApiNamespaceList::class)
    }

    fun getNamespace(namespaceId: NamespaceId): ApiNamespace {
        val getNamespaceResponse = httpTemplate.get("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespaceId)
            .execute()
        assertThat(getNamespaceResponse.statusCode, equalTo(Ok))
        require(getNamespaceResponse is SuccessHttpResponse) { "request was not successful" }
        return getNamespaceResponse.result(ApiNamespace::class)
    }
}