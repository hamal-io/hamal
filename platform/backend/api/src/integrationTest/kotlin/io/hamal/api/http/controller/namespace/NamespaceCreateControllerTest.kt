package io.hamal.api.http.controller.namespace

import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.sdk.api.ApiNamespaceAppendRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
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
}