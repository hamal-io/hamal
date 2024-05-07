package io.hamal.api.http.controller.namespace

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain._enum.Features.*
import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.lib.domain.vo.NamespaceName.Companion.NamespaceName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiNamespaceAppendRequest
import io.hamal.lib.sdk.api.ApiNamespaceUpdateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NamespaceUpdateControllerTest : NamespaceBaseControllerTest() {

    @Test
    fun `Tries to update namespace which does not exists`() {
        val getNamespaceResponse = httpTemplate.patch("/v1/namespaces/33333333")
            .body(ApiNamespaceUpdateRequest(NamespaceName("update")))
            .execute()

        assertThat(getNamespaceResponse.statusCode, equalTo(NotFound))
        require(getNamespaceResponse is HttpErrorResponse) { "request was successful" }

        val error = getNamespaceResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Namespace not found"))
    }

    @Test
    fun `Updates namespace name and features`() {
        val updatedFeatures = NamespaceFeatures(
            ValueObject.builder()
                .set(Endpoint.name.lowercase(), true)
                .set(Schedule.name.lowercase(), false)
                .set(Webhook.name.lowercase(), false)
                .build()
        )

        val namespaceId = awaitCompleted(appendNamespace(ApiNamespaceAppendRequest(NamespaceName("created-name"), NamespaceFeatures.default))).id

        awaitCompleted(updateNamespace(namespaceId, ApiNamespaceUpdateRequest(
            name = NamespaceName("updated-name"),
            features = updatedFeatures
        )))

        with(getNamespace(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(name, equalTo(NamespaceName("updated-name")))
            assertTrue(features.isActive(Endpoint))
            assertFalse(features.isActive(Schedule))
            assertFalse(features.isActive(Webhook))
        }
    }
}