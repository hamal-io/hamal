package io.hamal.api.http.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiNamespaceCreateReq
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class NamespaceCreateControllerTest : NamespaceBaseControllerTest() {
    @Test
    fun `Create namespace`() {
        val result = createNamespace(
            ApiNamespaceCreateReq(
                name = NamespaceName("test-namespace"),
                inputs = NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))
            )
        )
        awaitCompleted(result)
        verifyNamespaceCreated(result.namespaceId)
    }
}

private fun NamespaceCreateControllerTest.verifyNamespaceCreated(namespaceId: NamespaceId) {
    with(namespaceQueryRepository.get(namespaceId)) {
        assertThat(id, equalTo(namespaceId))
        assertThat(name, equalTo(NamespaceName("test-namespace")))
        assertThat(inputs, equalTo(NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
    }
}