package io.hamal.backend.web.namespace

import io.hamal.lib.domain.req.CreateNamespaceReq
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.domain.ApiError
import io.hamal.lib.sdk.hub.domain.ApiNamespace
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class GetNamespaceRouteTest : BaseNamespaceRouteTest() {
    @Test
    fun `Namespace does not exists`() {
        val getNamespaceResponse = httpTemplate.get("/v1/namespaces/33333333").execute()
        assertThat(getNamespaceResponse.statusCode, equalTo(NotFound))
        require(getNamespaceResponse is ErrorHttpResponse) { "request was successful" }

        val error = getNamespaceResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Namespace not found"))
    }

    @Test
    fun `Get namespace`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                CreateNamespaceReq(
                    name = NamespaceName("namespace-one"),
                    inputs = NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))
                )
            )
        ).id(::NamespaceId)

        val getNamespaceResponse = httpTemplate.get("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespaceId)
            .execute()

        assertThat(getNamespaceResponse.statusCode, equalTo(Ok))
        require(getNamespaceResponse is SuccessHttpResponse) { "request was not successful" }

        with(getNamespaceResponse.result(ApiNamespace::class)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(name, equalTo(NamespaceName("namespace-one")))
            assertThat(inputs, equalTo(NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
        }
    }
}