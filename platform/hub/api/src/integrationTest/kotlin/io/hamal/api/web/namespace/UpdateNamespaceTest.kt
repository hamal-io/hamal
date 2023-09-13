package io.hamal.api.web.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.HubCreateNamespaceReq
import io.hamal.lib.sdk.hub.HubError
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import io.hamal.lib.sdk.hub.HubUpdateNamespaceReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class UpdateNamespaceTest : BaseNamespaceRouteTest() {

    @Test
    fun `Tries to update namespace which does not exists`() {
        val getNamespaceResponse = httpTemplate.put("/v1/namespaces/33333333")
            .body(
                HubUpdateNamespaceReq(
                    name = NamespaceName("update"),
                    inputs = NamespaceInputs(),
                )
            )
            .execute()

        assertThat(getNamespaceResponse.statusCode, equalTo(NotFound))
        require(getNamespaceResponse is ErrorHttpResponse) { "request was successful" }

        val error = getNamespaceResponse.error(HubError::class)
        assertThat(error.message, equalTo("Namespace not found"))
    }

    @Test
    fun `Updates namespace`() {
        val namespace = awaitCompleted(
            createNamespace(
                HubCreateNamespaceReq(
                    name = NamespaceName("createdName"),
                    inputs = NamespaceInputs(MapType((mutableMapOf("hamal" to StringType("createdInputs")))))
                )
            )
        )

        val updateNamespaceResponse = httpTemplate.put("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespace.id)
            .body(
                HubUpdateNamespaceReq(
                    name = NamespaceName("updatedName"),
                    inputs = NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs"))))
                )
            )
            .execute()
        assertThat(updateNamespaceResponse.statusCode, equalTo(Accepted))
        require(updateNamespaceResponse is SuccessHttpResponse) { "request was not successful" }
        
        val req = updateNamespaceResponse.result(HubSubmittedReqWithId::class)
        val namespaceId = awaitCompleted(req).id(::NamespaceId)

        with(getNamespace(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(name, equalTo(NamespaceName("updatedName")))
            assertThat(inputs, equalTo(NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs"))))))
        }
    }
}