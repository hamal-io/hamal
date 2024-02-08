package io.hamal.api.http.controller.namespace

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.NamespaceType
import io.hamal.lib.sdk.api.ApiNamespaceCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class NamespaceCreateControllerTest : NamespaceBaseControllerTest() {
    @Test
    fun `Create namespace`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                ApiNamespaceCreateRequest(
                    name = NamespaceName("test-namespace"),
                    inputs = NamespaceInputs(HotObject.builder().set("hamal", "rocks").build()),
                    type = null
                )
            )
        ).namespaceId

        with(namespaceQueryRepository.get(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(type, equalTo(NamespaceType.default))
            assertThat(name, equalTo(NamespaceName("test-namespace")))
            assertThat(inputs, equalTo(NamespaceInputs(HotObject.builder().set("hamal", "rocks").build())))
        }
    }

    @Test
    fun `Create namespace with type`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                ApiNamespaceCreateRequest(
                    name = NamespaceName("test-namespace"),
                    inputs = NamespaceInputs(HotObject.builder().set("hamal", "rocks").build()),
                    type = NamespaceType("SpecialNamespaceType")
                )
            )
        ).namespaceId

        with(namespaceQueryRepository.get(namespaceId)) {
            assertThat(id, equalTo(namespaceId))
            assertThat(type, equalTo(NamespaceType("SpecialNamespaceType")))
            assertThat(name, equalTo(NamespaceName("test-namespace")))
            assertThat(inputs, equalTo(NamespaceInputs(HotObject.builder().set("hamal", "rocks").build())))
        }
    }
}