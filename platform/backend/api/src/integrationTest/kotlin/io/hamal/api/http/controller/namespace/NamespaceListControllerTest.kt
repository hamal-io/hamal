package io.hamal.api.http.controller.namespace

import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.NamespaceType
import io.hamal.lib.sdk.api.ApiNamespaceCreateRequest
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
            assertThat(type, equalTo(NamespaceType.default))
        }
    }

    @Test
    fun `Single namespace`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                ApiNamespaceCreateRequest(
                    name = NamespaceName("namespace-one"),
                    inputs = NamespaceInputs(),
                    type = NamespaceType.default

                )
            )
        ).namespaceId

        with(listNamespaces()) {
            assertThat(namespaces, hasSize(2))
            with(namespaces.first()) {
                assertThat(id, equalTo(namespaceId))
                assertThat(name, equalTo(NamespaceName("namespace-one")))
                assertThat(type, equalTo(NamespaceType.default))
            }
        }
    }

    @Test
    fun `Limit namespaces`() {
        awaitCompleted(
            IntRange(0, 20).map {
                createNamespace(
                    ApiNamespaceCreateRequest(
                        name = NamespaceName("namespace-$it"),
                        inputs = NamespaceInputs(),
                        type = NamespaceType.default
                    )
                )
            }
        )

        val listResponse = httpTemplate.get("/v1/groups/{groupId}/namespaces")
            .path("groupId", testGroup.id)
            .parameter("limit", 12)
            .execute(ApiNamespaceList::class)

        assertThat(listResponse.namespaces, hasSize(12))

        listResponse.namespaces.forEachIndexed { idx, namespace ->
            assertThat(namespace.name, equalTo(NamespaceName("namespace-${(20 - idx)}")))
        }
    }

    @Test
    fun `Skip and limit namespaces`() {
        val requests = IntRange(0, 99).map {
            createNamespace(
                ApiNamespaceCreateRequest(
                    name = NamespaceName("namespace-$it"),
                    inputs = NamespaceInputs(),
                    type = NamespaceType.default
                )
            )
        }

        awaitCompleted(requests)
        val fortyNinth = requests[49]

        val listResponse = httpTemplate.get("/v1/groups/{groupId}/namespaces")
            .path("groupId", testGroup.id)
            .parameter("after_id", fortyNinth.namespaceId)
            .parameter("limit", 1)
            .execute(ApiNamespaceList::class)

        assertThat(listResponse.namespaces, hasSize(1))

        val namespace = listResponse.namespaces.first()
        assertThat(namespace.name, equalTo(NamespaceName("namespace-48")))
    }
}