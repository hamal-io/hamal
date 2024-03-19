package io.hamal.api.http.controller.namespace

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.NamespaceFeature.*
import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.sdk.api.ApiNamespaceAppendRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NamespaceCreateControllerTest : NamespaceBaseControllerTest() {
    @Test
    fun `Create namespace`() {
        val namespaceId = awaitCompleted(
            appendNamespace(ApiNamespaceAppendRequest(NamespaceName("test-namespace")))
        ).id

        with(namespaceQueryRepository.get(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(name, equalTo(NamespaceName("test-namespace")))
        }
    }

    @Test
    fun `Creates namespace with limited features`() {
        val featuresRequest = NamespaceFeatures(
            HotObject.builder()
                .set(Endpoint.name, false)
                .build()
        )

        val namespaceId = awaitCompleted(
            appendNamespace(
                ApiNamespaceAppendRequest(
                    name = NamespaceName("test-namespace"),
                    features = featuresRequest
                )
            )
        ).id
        val z = namespaceQueryRepository.get(namespaceId)

        with(namespaceQueryRepository.get(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(name, equalTo(NamespaceName("test-namespace")))
            assertThat(features, equalTo(features))
            assertTrue(features.hasFeature(Schedule))
            assertTrue(features.hasFeature(Topic))
            assertTrue(features.hasFeature(Webhook))
            assertFalse(features.hasFeature(Endpoint))
        }
    }
}