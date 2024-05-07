package io.hamal.api.http.controller.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.NamespaceName.Companion.NamespaceName
import io.hamal.lib.sdk.api.ApiNamespaceAppendRequest
import io.hamal.lib.sdk.api.ApiNamespaceList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
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
        ).id

        with(listNamespaces()) {
            assertThat(namespaces, hasSize(2))

            with(namespaces[0]) {
                assertThat(id, equalTo(namespaceId))
                assertThat(parentId, equalTo(NamespaceId.root))
                assertThat(name, equalTo(NamespaceName("hamal::namespace-one")))
            }

            with(namespaces[1]) {
                assertThat(id, equalTo(NamespaceId.root))
                assertThat(parentId, equalTo(NamespaceId.root))
                assertThat(name, equalTo(NamespaceName("hamal")))
            }
        }
    }


    @Test
    fun `Limit namespaces`() {
        awaitCompleted(
            IntRange(0, 20).map {
                appendNamespace(ApiNamespaceAppendRequest(NamespaceName("namespace-$it")))
            }
        )

        val listResponse = httpTemplate.get("/v1/workspaces/539/namespaces")
            .parameter("limit", 12)
            .execute(ApiNamespaceList::class)

        assertThat(listResponse.namespaces, hasSize(12))

        listResponse.namespaces.forEachIndexed { idx, namespace ->
            assertThat(namespace.parentId, equalTo(NamespaceId(1337)))
            assertThat(namespace.name, equalTo(NamespaceName("hamal::namespace-${(20 - idx)}")))
        }
    }

    @Test
    fun `Skip and limit namespaces`() {
        val requests = IntRange(0, 99).map {
            appendNamespace(ApiNamespaceAppendRequest(NamespaceName("namespace-$it")))
        }

        awaitCompleted(requests)
        val fortyNinth = requests[49]

        val listResponse = httpTemplate.get("/v1/workspaces/539/namespaces")
            .parameter("after_id", fortyNinth.id)
            .parameter("limit", 1)
            .execute(ApiNamespaceList::class)

        assertThat(listResponse.namespaces, hasSize(1))

        val namespace = listResponse.namespaces.first()
        assertThat(namespace.parentId, equalTo(NamespaceId(1337)))
        assertThat(namespace.name, equalTo(NamespaceName("hamal::namespace-48")))
    }
}