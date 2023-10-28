package io.hamal.api.http.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiNamespaceCreateReq
import io.hamal.lib.sdk.api.ApiNamespaceUpdateReq
import io.hamal.lib.sdk.toReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class NamespaceUpdateControllerTest : NamespaceBaseControllerTest() {

    @Test
    fun `Tries to update namespace which does not exists`() {
        val getNamespaceResponse = httpTemplate.patch("/v1/namespaces/33333333")
            .body(
                ApiNamespaceUpdateReq(
                    name = NamespaceName("update"),
                    inputs = NamespaceInputs(),
                )
            )
            .execute()

        assertThat(getNamespaceResponse.statusCode, equalTo(NotFound))
        require(getNamespaceResponse is HttpErrorResponse) { "request was successful" }

        val error = getNamespaceResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Namespace not found"))
    }

    @Test
    fun `Updates namespace`() {
        val namespace = awaitCompleted(
            createNamespace(
                ApiNamespaceCreateReq(
                    name = NamespaceName("createdName"),
                    inputs = NamespaceInputs(MapType((mutableMapOf("hamal" to StringType("createdInputs")))))
                )
            )
        )

        val updateNamespaceResponse = httpTemplate.patch("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespace.id)
            .body(
                ApiNamespaceUpdateReq(
                    name = NamespaceName("updatedName"),
                    inputs = NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs"))))
                )
            )
            .execute()
        assertThat(updateNamespaceResponse.statusCode, equalTo(Accepted))
        require(updateNamespaceResponse is HttpSuccessResponse) { "request was not successful" }

        val req = updateNamespaceResponse.toReq<NamespaceId>()
        val namespaceId = awaitCompleted(req).id

        with(getNamespace(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(name, equalTo(NamespaceName("updatedName")))
            assertThat(inputs, equalTo(NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("updatedInputs"))))))
        }
    }
}