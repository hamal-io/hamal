package io.hamal.api.http.controller.namespace

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain._enum.NamespaceFeatures.*
import io.hamal.lib.domain.vo.NamespaceFeature.Companion.NamespaceFeature
import io.hamal.lib.domain.vo.NamespaceFeaturesMap
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
    fun `Updates namespace name and features`() {
        val updatedFeatures = NamespaceFeaturesMap(
            ValueObject.builder()
                .set(NamespaceFeature(schedule).stringValue, 0)
                .set(webhook.name, 0)
                .build()
        )

        val namespaceId = awaitCompleted(
            appendNamespace(
                ApiNamespaceAppendRequest(NamespaceName("created-name"))
            )
        ).id



        awaitCompleted(
            updateNamespace(
                namespaceId,
                ApiNamespaceUpdateRequest(
                    features = updatedFeatures
                )
            )
        )

        awaitCompleted(
            updateNamespace(
                namespaceId,
                ApiNamespaceUpdateRequest(
                    name = NamespaceName("updated-name")
                )
            )
        )

        with(getNamespace(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(name, equalTo(NamespaceName("updated-name")))
            assertTrue(features.hasFeature(NamespaceFeature(schedule)))
            assertTrue(features.hasFeature(NamespaceFeature(webhook)))
            assertFalse(features.hasFeature(NamespaceFeature(endpoint)))
            assertFalse(features.hasFeature(NamespaceFeature(topic)))
        }
    }
}