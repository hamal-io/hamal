package io.hamal.api.http.controller.namespace

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.NamespaceType
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiNamespace
import io.hamal.lib.sdk.api.ApiNamespaceCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class NamespaceGetControllerTest : NamespaceBaseControllerTest() {
    @Test
    fun `Namespace does not exists`() {
        val getNamespaceResponse = httpTemplate.get("/v1/namespaces/33333333").execute()
        assertThat(getNamespaceResponse.statusCode, equalTo(NotFound))
        require(getNamespaceResponse is HttpErrorResponse) { "request was successful" }

        val error = getNamespaceResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Namespace not found"))
    }

    @Test
    fun `Get namespace`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                ApiNamespaceCreateRequest(
                    name = NamespaceName("namespace-one"),
                    inputs = NamespaceInputs(HotObject.builder().set("hamal", "rocks").build()),
                    type = null
                )
            )
        ).namespaceId

        val getNamespaceResponse = httpTemplate.get("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespaceId)
            .execute()

        assertThat(getNamespaceResponse.statusCode, equalTo(Ok))
        require(getNamespaceResponse is HttpSuccessResponse) { "request was not successful" }

        with(getNamespaceResponse.result(ApiNamespace::class)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(type, equalTo(NamespaceType.default))
            assertThat(name, equalTo(NamespaceName("namespace-one")))
            assertThat(inputs, equalTo(NamespaceInputs(HotObject.builder().set("hamal", "rocks").build())))
        }
    }
}