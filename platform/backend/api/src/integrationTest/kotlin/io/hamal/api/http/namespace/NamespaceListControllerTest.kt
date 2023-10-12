package io.hamal.api.http.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.sdk.api.ApiCreateNamespaceReq
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
    fun `Single namespace`() {
        val namespaceId = awaitCompleted(
            createNamespace(
                ApiCreateNamespaceReq(
                    name = NamespaceName("namespace-one"),
                    inputs = NamespaceInputs()
                )
            )
        ).id(::NamespaceId)

        with(listNamespaces()) {
            assertThat(namespaces, hasSize(2))
            with(namespaces.first()) {
                assertThat(id, equalTo(namespaceId))
                assertThat(name, equalTo(NamespaceName("namespace-one")))
            }
        }
    }

    @Test
    fun `Limit namespaces`() {
        awaitCompleted(
            IntRange(0, 20).map {
                createNamespace(
                    ApiCreateNamespaceReq(
                        name = NamespaceName("namespace-$it"),
                        inputs = NamespaceInputs()
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
                ApiCreateNamespaceReq(
                    name = NamespaceName("namespace-$it"),
                    inputs = NamespaceInputs()
                )
            )
        }

        awaitCompleted(requests)
        val fortyNinth = requests[49]

        val listResponse = httpTemplate.get("/v1/groups/{groupId}/namespaces")
            .path("groupId", testGroup.id)
            .parameter("after_id", fortyNinth.id)
            .parameter("limit", 1)
            .execute(ApiNamespaceList::class)

        assertThat(listResponse.namespaces, hasSize(1))

        val namespace = listResponse.namespaces.first()
        assertThat(namespace.name, equalTo(NamespaceName("namespace-48")))
    }
}