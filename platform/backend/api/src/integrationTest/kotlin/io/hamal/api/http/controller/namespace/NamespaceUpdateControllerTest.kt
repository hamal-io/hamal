package io.hamal.api.http.controller.namespace

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.NamespaceFeature
import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiNamespaceAppendRequest
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
                ApiNamespaceUpdateRequest(NamespaceName("update"))
            )
            .execute()

        assertThat(getNamespaceResponse.statusCode, equalTo(NotFound))
        require(getNamespaceResponse is HttpErrorResponse) { "request was successful" }

        val error = getNamespaceResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Namespace not found"))
    }

    @Test
    fun `Updates namespace name`() {
        val namespace = awaitCompleted(
            appendNamespace(
                ApiNamespaceAppendRequest(NamespaceName("created-name"))
            )
        )

        val updateNamespaceResponse = httpTemplate.patch("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespace.id)
            .body(ApiNamespaceUpdateRequest(NamespaceName("updated-name")))
            .execute()
        assertThat(updateNamespaceResponse.statusCode, equalTo(Accepted))
        require(updateNamespaceResponse is HttpSuccessResponse) { "request was not successful" }

        val req = updateNamespaceResponse.result(ApiNamespaceUpdateRequested::class)
        val namespaceId = awaitCompleted(req).id

        with(getNamespace(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(name, equalTo(NamespaceName("updated-name")))
            assertThat(features, equalTo(NamespaceFeatures.default))
        }
    }

    @Test
    fun `Updates namespace name and features`() {
        val updatedFeatures = NamespaceFeatures(
            HotObject.builder()
                .set(NamespaceFeature.SCHEDULES.name, true)
                .set(NamespaceFeature.TOPICS.name, false)
                .set(NamespaceFeature.WEBHOOKS.name, true)
                .set(NamespaceFeature.ENDPOINTS.name, false)
                .build()
        )

        val namespace = awaitCompleted(
            appendNamespace(
                ApiNamespaceAppendRequest(NamespaceName("created-name"))
            )
        )

        val updateNamespaceResponse = httpTemplate.patch("/v1/namespaces/{namespaceId}")
            .path("namespaceId", namespace.id)
            .body(
                ApiNamespaceUpdateRequest(
                    name = NamespaceName("updated-name"),
                    features = updatedFeatures
                )
            )
            .execute()
        assertThat(updateNamespaceResponse.statusCode, equalTo(Accepted))
        require(updateNamespaceResponse is HttpSuccessResponse) { "request was not successful" }

        val req = updateNamespaceResponse.result(ApiNamespaceUpdateRequested::class)
        val namespaceId = awaitCompleted(req).id

        with(getNamespace(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(name, equalTo(NamespaceName("updated-name")))
            assertThat(features, equalTo(updatedFeatures))
        }
    }

}