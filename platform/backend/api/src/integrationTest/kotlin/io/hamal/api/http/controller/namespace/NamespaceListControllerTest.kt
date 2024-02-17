package io.hamal.api.http.controller.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.sdk.api.ApiNamespaceAppendRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class NamespaceListControllerTest : NamespaceBaseControllerTest() {
    @Test
    fun `Only default namespaces`() {
        val result = listNamespaces()
        assertThat(result.namespaces, hasSize(1))

        with(result.namespaces.first()) {
            assertThat(name, equalTo(NamespaceName("hamal")))
        }
    }

    @Test
    fun `List namespaces`() {
        val namespaceId = awaitCompleted(
            appendNamespace(ApiNamespaceAppendRequest(NamespaceName("namespace-one")))
        ).namespaceId

        with(listNamespaces()) {
            assertThat(namespaces, hasSize(1))
            with(namespaces.first()) {
                assertThat(id, equalTo(NamespaceId.root))
                assertThat(name, equalTo(NamespaceName("hamal")))
                assertThat(children, hasSize(1))

                with(children.first()) {
                    assertThat(id, equalTo(namespaceId))
                    assertThat(name, equalTo(NamespaceName("namespace-one")))
                    assertThat(children, empty())
                }
            }
        }
    }
}