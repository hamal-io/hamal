package io.hamal.backend.web.namespace

import io.hamal.lib.domain.req.CreateNamespaceReq
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class CreateNamespaceRouteTest : BaseNamespaceRouteTest() {
    @Test
    fun `Create namespace`() {
        val result = createNamespace(
            CreateNamespaceReq(
                name = NamespaceName("test-namespace"),
                inputs = NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))
            )
        )
        awaitCompleted(result.reqId)
        verifyNamespaceCreated(result.id(::NamespaceId))
    }
}

private fun CreateNamespaceRouteTest.verifyNamespaceCreated(namespaceId: NamespaceId) {
    with(namespaceQueryRepository.get(namespaceId)) {
        assertThat(id, equalTo(namespaceId))
        assertThat(name, equalTo(NamespaceName("test-namespace")))
        assertThat(inputs, equalTo(NamespaceInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
    }
}