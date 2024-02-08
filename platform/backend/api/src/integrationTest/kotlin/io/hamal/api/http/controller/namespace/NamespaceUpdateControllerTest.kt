package io.hamal.api.http.controller.namespace

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.NamespaceType
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiNamespaceCreateRequest
import io.hamal.lib.sdk.api.ApiNamespaceUpdateRequest
import io.hamal.lib.sdk.api.ApiNamespaceUpdateRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class NamespaceUpdateControllerTest : NamespaceBaseControllerTest() {

    @Test
    fun `Tries to update namespace which does not exists`() {
        val getNamespaceResponse = httpTemplate.patch("/v1/namespaces/33333333")
            .body(
                ApiNamespaceUpdateRequest(
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
                ApiNamespaceCreateRequest(
                    name = NamespaceName("created-name"),
                    inputs = NamespaceInputs(HotObject.builder().set("hamal", "createdInputs").build()),
                    type = NamespaceType.default
                )
            )
        )

        val updateNamespaceResponse = httpTemplate.patch("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespace.namespaceId)
            .body(
                ApiNamespaceUpdateRequest(
                    name = NamespaceName("updated-name"),
                    inputs = NamespaceInputs(HotObject.builder().set("hamal", "updatedInputs").build())
                )
            )
            .execute()
        assertThat(updateNamespaceResponse.statusCode, equalTo(Accepted))
        require(updateNamespaceResponse is HttpSuccessResponse) { "request was not successful" }

        val req = updateNamespaceResponse.result(ApiNamespaceUpdateRequested::class)
        val namespaceId = awaitCompleted(req).namespaceId

        with(getNamespace(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(name, equalTo(NamespaceName("updated-name")))
            assertThat(inputs, equalTo(NamespaceInputs(HotObject.builder().set("hamal", "updatedInputs").build())))
            assertThat(type, equalTo(NamespaceType.default))
        }
    }
}