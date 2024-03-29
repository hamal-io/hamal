package io.hamal.api.http.controller.namespace

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.NamespaceFeature.*
import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.lib.domain.vo.NamespaceName
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
        val updatedFeatures = NamespaceFeatures(
            HotObject.builder()
                .set(schedule.name, 0)
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
            assertTrue(features.hasFeature(schedule))
            assertTrue(features.hasFeature(webhook))
            assertFalse(features.hasFeature(endpoint))
            assertFalse(features.hasFeature(topic))
        }
    }
}